package com.droidpop.service;

import android.app.Service;
import android.os.Binder;

public abstract class LocalBinder extends Binder {
	public abstract Service getService();
}
