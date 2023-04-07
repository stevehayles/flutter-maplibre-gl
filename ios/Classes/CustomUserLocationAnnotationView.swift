import Foundation
import Mapbox
import QuartzCore

class CustomUserLocationAnnotationView: MGLUserLocationAnnotationView {
    /// The foreground image of the location puck.
    var puckImage: UIImage?
    
    /// The CA layer that draws the foreground image.
    private var puckImageLayer: CALayer?
    
    override func layoutSubviews() {
        if let puckImageLayer = puckImageLayer {
            puckImageLayer.removeFromSuperlayer()
        }
        if let puckImage = puckImage {
            puckImageLayer = CALayer()
            puckImageLayer!.contents = puckImage.cgImage
            puckImageLayer!.bounds = bounds
            puckImageLayer!.position = CGPoint(x: bounds.midX, y: bounds.midY)
            layer.addSublayer(puckImageLayer!)
        }
    }
    
    override func update() {
        let heading = -MGLRadiansFromDegrees((mapView?.direction ?? 0) - (mapView?.userLocation?.location?.course ?? 0))
        let headingRot = CATransform3DRotate(CATransform3DIdentity, heading, 0, 0, 1)
        let pitch = MGLRadiansFromDegrees(Double(mapView?.camera.pitch ?? 0))
        let pitchRot = CATransform3DRotate(CATransform3DIdentity, pitch, 1, 0, 0)
        layer.sublayerTransform = CATransform3DConcat(headingRot, pitchRot)
    }
}