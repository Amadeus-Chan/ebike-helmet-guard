package net.czming.detection.review;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

public class OrtGpuCloseTest {
    public static void main(String[] args) throws Exception {
        OrtEnvironment env = OrtEnvironment.getEnvironment();

        OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
        opts.addCUDA(0);

        try (OrtSession session = env.createSession("C:\\Users\\likeai\\Desktop\\ebike-helmet-guard\\services\\detection-review\\src\\main\\resources\\model\\detect_model\\best.onnx", opts)) {
            System.out.println("session created");
        }

        env.close();
        System.out.println("closed");
    }
}