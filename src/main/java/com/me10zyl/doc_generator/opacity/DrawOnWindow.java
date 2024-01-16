package com.me10zyl.doc_generator.opacity;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.ptr.IntByReference;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


class DrawingPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 在绘图面板上进行绘图
        drawSomething(g);
    }

    private void drawSomething(Graphics g) {
        // 在这里进行绘图操作，例如绘制一个矩形和一个圆形
        g.setColor(Color.RED);
        g.fillRect(0, 0, 100, 100);
//
//        g.setColor(Color.RED);
//        g.drawOval(200, 100, 80, 80);
    }
}

public class DrawOnWindow {

    public static class MyWindowListener implements WinUser.WindowProc {
        public HWND windowHandle;

        @Override
        public WinDef.LRESULT callback(HWND hwnd, int uMsg, WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
            if (uMsg == 0x0003 && hwnd.equals(windowHandle)) {
                int x = lParam.intValue() & 0xFFFF;
                int y = (lParam.intValue() >> 16) & 0xFFFF;

                onWindowMoved(x, y);
            }

            return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
        }
    }

    private static void onWindowMoved(int x, int y) {
        System.out.println("Window moved to (" + x + ", " + y + ")");
    }

    public static void main(String[] args) {
        // 请替换为要绘制的窗口的标题
        String targetWindowTitle = "微信";

        // 获取目标窗口句柄
        HWND targetWindowHwnd = User32.INSTANCE.FindWindow(null, targetWindowTitle);
        if (targetWindowHwnd == null) {
            System.out.println("未找到目标窗口：" + targetWindowTitle);
            return;
        }

        SwingUtilities.invokeLater(DrawOnWindow::createUI);

        MyWindowListener windowListener = new MyWindowListener();
        windowListener.windowHandle = targetWindowHwnd;


        System.out.println("微信hwnd:" + targetWindowHwnd);

//        User32.INSTANCE.SetWindowLong(targetWindowHwnd, WinUser.GWL_WNDPROC, windowListener);

        WinUser.MSG msg = new WinUser.MSG();
        while (User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
            System.out.println("trigger msg" + msg);
            User32.INSTANCE.TranslateMessage(msg);
            User32.INSTANCE.DispatchMessage(msg);
        }


//        // 获取目标窗口设备上下文
//        HDC hdc = User32.INSTANCE.GetDC(targetWindowHwnd);
//
//        // 创建一个图像进行绘制
//        BufferedImage image = createImage();
//
//        // 获取图像数据
//        int[] pixels = new int[image.getWidth() * image.getHeight()];
//        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
//
//        // 创建位图信息
//        BITMAPINFO bmi = new BITMAPINFO();
//        bmi.bmiHeader.biWidth = image.getWidth();
//        bmi.bmiHeader.biHeight = -image.getHeight(); // 负值表示自上而下
//        bmi.bmiHeader.biPlanes = 1;
//        bmi.bmiHeader.biBitCount = 32;
//        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

        // 将图像数据绘制到目标窗口
//        GDI32.INSTANCE.StretchDIBits(hdc, 0, 0, image.getWidth(), image.getHeight(),
//                0, 0, image.getWidth(), image.getHeight(),
//                pixels, bmi, WinGDI.DIB_RGB_COLORS, WinGDI.SRCCOPY);

        // 释放设备上下文
//        User32.INSTANCE.ReleaseDC(targetWindowHwnd, hdc);
    }

    private static void createUI() {
        JFrame frame = new JFrame("Drawing Example");
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(100, 100);

        // 创建一个自定义的绘图面板
        DrawingPanel drawingPanel = new DrawingPanel();
        frame.add(drawingPanel);
        frame.setVisible(true);
    }

    private static BufferedImage createImage() {
        // 创建一个简单的图像，这里绘制一个红色矩形
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(50, 50, 100, 100);
        g2d.dispose();
        return image;
    }
}