package com.me10zyl.doc_generator.ps5;

import cn.hutool.core.util.StrUtil;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class ChiakiHelper {

    private static Map<Integer, String> hotKeys = new HashMap<Integer, String>() {{
        put(NativeKeyEvent.VC_F8, "exit");
    }};

    private static Map<String, Integer> keyMappings = new HashMap<String, Integer>() {{
        put("left", KeyEvent.VK_LEFT);
        put("right", KeyEvent.VK_RIGHT);
        put("up", KeyEvent.VK_UP);
        put("down", KeyEvent.VK_DOWN);
    }};

    private boolean enableLogging = true;

    private Robot robot;

    @SneakyThrows
    public void keyPress(String key) {
        if (this.robot == null) {
            this.robot = new Robot();
        }
        Integer keycode = keyMappings.get(key);
        if (keycode == null) {
            return;
        }
        robot.keyPress(keycode);
        robot.keyRelease(keycode);
    }

    public void log(String msg, Object... args) {
        if (enableLogging) {
            System.out.println(StrUtil.format(msg, args));
        }
    }

    static class KeyListener implements NativeKeyListener {
        private ChiakiHelper chiakiHelper;

        public KeyListener(ChiakiHelper chiakiHelper) {
            this.chiakiHelper = chiakiHelper;
        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
            hotKeys.forEach((k, v) -> {
                if (k == nativeEvent.getKeyCode()) {
                    String cmd = hotKeys.get(k);
                    chiakiHelper.log("key pressed: {}.{}", nativeEvent.getKeyChar(), cmd);
                    switch (cmd) {
                        case "exit":
                            System.exit(0);
                            break;
                    }
                }
            });
        }
    }

    static class MouseListener implements NativeMouseInputListener {

        private ChiakiHelper chiakiHelper;
        private Point mousePoint;

        public MouseListener(ChiakiHelper chiakiHelper) {
            this.mousePoint = MouseInfo.getPointerInfo().getLocation();
            this.chiakiHelper = chiakiHelper;
        }

        @Override
        public void nativeMouseClicked(NativeMouseEvent nativeEvent) {
            NativeMouseInputListener.super.nativeMouseClicked(nativeEvent);
        }

        @Override
        public void nativeMousePressed(NativeMouseEvent nativeEvent) {

        }

        @Override
        public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
            NativeMouseInputListener.super.nativeMouseReleased(nativeEvent);
        }


        @SneakyThrows
        @Override
        public void nativeMouseMoved(NativeMouseEvent nativeEvent) {

            Point point = nativeEvent.getPoint();
            double deltaX = point.getX() - mousePoint.getX();
            double deltaY = point.getY() - mousePoint.getY();
            if (deltaX < 0) {
                chiakiHelper.log("left" + Math.abs(deltaX));
                chiakiHelper.keyPress("left");
            }
            if (deltaX > 0) {
                chiakiHelper.log("right" + Math.abs(deltaX));
                chiakiHelper.keyPress("right");
            }
            if (deltaY < 0) {
                chiakiHelper.log("up" + Math.abs(deltaY));
                chiakiHelper.keyPress("up");
            }
            if (deltaY > 0) {
                chiakiHelper.log("down" + Math.abs(deltaY));
                chiakiHelper.keyPress("down");
            }
            mousePoint = point;
        }

        @Override
        public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
            NativeMouseInputListener.super.nativeMouseDragged(nativeEvent);
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        ChiakiHelper chiakiHelper = new ChiakiHelper();

        // Construct the example object.
        MouseListener example = new MouseListener(chiakiHelper);

        KeyListener example2 = new KeyListener(chiakiHelper);


        // Add the appropriate listeners.
//        GlobalScreen.addNativeKeyListener(example2);
        GlobalScreen.addNativeMouseListener(example);
        GlobalScreen.addNativeMouseMotionListener(example);
    }
}
