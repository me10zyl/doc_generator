package com.me10zyl.doc_generator.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class SSEController {
    private ExecutorService executor = Executors.newCachedThreadPool();


    @GetMapping("/sseStream")
    public SseEmitter handleSse() {
        SseEmitter emitter = new SseEmitter();
        executor.execute(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    emitter.send(SseEmitter.event().name("message").data("message " + i));
//                    emitter.send("message " + i, MediaType.TEXT_PLAIN);
                }
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

}
