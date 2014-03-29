package me.wtao.service;

interface IScreenCaptureService
{
    void setDebuggable(boolean enable);
    Bitmap takeScreenCapture();
}