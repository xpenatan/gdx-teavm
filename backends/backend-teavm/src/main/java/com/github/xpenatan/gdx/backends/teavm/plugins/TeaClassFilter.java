package com.github.xpenatan.gdx.backends.teavm.plugins;

import org.teavm.model.FieldReference;
import org.teavm.model.MethodReference;
import org.teavm.vm.spi.ElementFilter;

public class TeaClassFilter implements ElementFilter {

	@Override
	public boolean acceptClass (String className) {
		return true;
	}

	@Override
	public boolean acceptMethod (MethodReference method) {
		return true;
	}

	@Override
	public boolean acceptField (FieldReference field) {
		return true;
	}
}
