import Mapbox

/// A location manager that implements the MGLLocationManager protocol.
/// This location manager proxies the setting of a custom location. Based on:
/// https://github.com/mapbox/mapbox-gl-native-ios/blob/main/platform/darwin/src/MGLLocationManager.m
class CustomLocationManager : NSObject, MGLLocationManager {
    /// The overridden location.
    var overriddenLocation: CLLocation? {
        didSet {
            if let overriddenLocation = overriddenLocation {
                delegate?.locationManager(self, didUpdate: [overriddenLocation])
            }
        }
    }
    
    /// The overridden heading.
    var overriddenHeading: CLHeading? {
        didSet {
            if let overriddenHeading = overriddenHeading {
                delegate?.locationManager(self, didUpdate: overriddenHeading)
            }
        }
    }
    
    /// The heading orientation of the proxied location manager.
    var headingOrientation: CLDeviceOrientation {
        get { proxiedLocationManager.headingOrientation }
        set { proxiedLocationManager.headingOrientation = newValue }
    }
    
    /// The delegate of the location manager.
    var delegate: MGLLocationManagerDelegate?
    
    /// The proxied location manager.
    let proxiedLocationManager = CLLocationManager()
    
    /// The authorization status of the proxied location manager.
    var authorizationStatus: CLAuthorizationStatus {
        if #available(iOS 14.0, *) {
            return proxiedLocationManager.authorizationStatus
        } else {
            return CLLocationManager.authorizationStatus()
        }
    }
    
    /// Initialize the custom location manager.
    override init() {
        super.init()
        proxiedLocationManager.delegate = self
    }
    
    /// Deinitialize the custom location manager.
    deinit {
        proxiedLocationManager.stopUpdatingLocation()
        proxiedLocationManager.stopUpdatingHeading()
        proxiedLocationManager.delegate = nil
        delegate = nil
    }
    
    // MARK: - Proxied functions of the location manager.
    
    func requestAlwaysAuthorization() {
        print("Custom location manager: requestAlwaysAuthorization")
        proxiedLocationManager.requestAlwaysAuthorization()
    }
    
    func requestWhenInUseAuthorization() {
        print("Custom location manager: requestWhenInUseAuthorization")
        proxiedLocationManager.requestWhenInUseAuthorization()
    }
    
    func startUpdatingLocation() {
        print("Custom location manager: startUpdatingLocation")
        proxiedLocationManager.startUpdatingLocation()
        
        if let overriddenLocation = overriddenLocation {
            delegate?.locationManager(self, didUpdate: [overriddenLocation])
        }
    }
    
    func stopUpdatingLocation() {
        print("Custom location manager: stopUpdatingLocation")
        proxiedLocationManager.stopUpdatingLocation()
    }
    
    func startUpdatingHeading() {
        print("Custom location manager: startUpdatingHeading")
        proxiedLocationManager.startUpdatingHeading()
        
        if let overriddenHeading = overriddenHeading {
            delegate?.locationManager(self, didUpdate: overriddenHeading)
        }
    }
    
    func stopUpdatingHeading() {
        print("Custom location manager: stopUpdatingHeading")
        proxiedLocationManager.stopUpdatingHeading()
    }
    
    func dismissHeadingCalibrationDisplay() {
        print("Custom location manager: dismissHeadingCalibrationDisplay")
        proxiedLocationManager.dismissHeadingCalibrationDisplay()
    }
}

extension CustomLocationManager : CLLocationManagerDelegate {
    // MARK: - Proxied functions of the location manager delegate.
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard overriddenLocation == nil else { return }
        delegate?.locationManager(self, didUpdate: locations)
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateHeading newHeading: CLHeading) {
        guard overriddenHeading == nil else { return }
        delegate?.locationManager(self, didUpdate: newHeading)
    }
    
    func locationManagerShouldDisplayHeadingCalibration(_ manager: CLLocationManager) -> Bool {
        delegate?.locationManagerShouldDisplayHeadingCalibration(self) ?? false
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        delegate?.locationManager(self, didFailWithError: error)
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        delegate?.locationManagerDidChangeAuthorization(self)
    }
}