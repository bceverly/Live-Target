//
//  CameraView.swift
//  Live Target
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI
import AVFoundation
import os.log

struct CameraView: UIViewRepresentable {
    @Binding var capturedImage: UIImage?
    @Binding var zoomFactor: CGFloat
    
    func makeUIView(context: Context) -> UIView {
        let view = UIView()
        
        let captureSession = AVCaptureSession()
        captureSession.sessionPreset = .medium
        
        guard let captureDevice = AVCaptureDevice.default(for: .video),
              let input = try? AVCaptureDeviceInput(device: captureDevice) else {
            return view
        }
        
        captureSession.addInput(input)
        
        let previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        previewLayer.videoGravity = .resizeAspectFill
        previewLayer.frame = view.bounds
        view.layer.addSublayer(previewLayer)
        
        let output = AVCaptureVideoDataOutput()
        output.setSampleBufferDelegate(context.coordinator, queue: DispatchQueue.main)
        captureSession.addOutput(output)
        
        // Store session and device in coordinator instead of state
        context.coordinator.captureSession = captureSession
        context.coordinator.captureDevice = captureDevice
        
        DispatchQueue.global(qos: .userInitiated).async {
            captureSession.startRunning()
        }
        
        return view
    }
    
    func updateUIView(_ uiView: UIView, context: Context) {
        if let layer = uiView.layer.sublayers?.first as? AVCaptureVideoPreviewLayer {
            layer.frame = uiView.bounds
        }
        
        // Update zoom factor
        context.coordinator.updateZoom(zoomFactor)
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, AVCaptureVideoDataOutputSampleBufferDelegate {
        let parent: CameraView
        var captureSession: AVCaptureSession?
        var captureDevice: AVCaptureDevice?
        private var lastUpdateTime = Date()
        private let updateInterval: TimeInterval = 1.0/15.0 // 15 FPS max to reduce updates
        private let logger = Logger(subsystem: "com.bceassociates.Live-Target", category: "CameraView")
        
        init(_ parent: CameraView) {
            self.parent = parent
        }
        
        func updateZoom(_ zoomFactor: CGFloat) {
            guard let device = captureDevice else { return }
            
            do {
                try device.lockForConfiguration()
                device.videoZoomFactor = max(1.0, min(zoomFactor, device.activeFormat.videoMaxZoomFactor))
                device.unlockForConfiguration()
            } catch {
                logger.error("Error setting zoom: \(error.localizedDescription)")
            }
        }
        
        func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
            let now = Date()
            guard now.timeIntervalSince(lastUpdateTime) >= updateInterval else { return }
            lastUpdateTime = now
            
            guard let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else { return }
            
            let ciImage = CIImage(cvPixelBuffer: pixelBuffer)
            let context = CIContext()
            
            guard let cgImage = context.createCGImage(ciImage, from: ciImage.extent) else { return }
            
            DispatchQueue.main.async {
                self.parent.capturedImage = UIImage(cgImage: cgImage)
            }
        }
    }
}