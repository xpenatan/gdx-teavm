package com.badlogic.gdx.backends.dragome.js.typedarrays;

import com.dragome.commons.DelegateCode;

public interface TypedArray<ArrayType, ItemType>
{
	int get_length();

	@DelegateCode(eval= "this.node[$1]")
	ItemType get(int index);

	@DelegateCode(eval= "this.node[$1] = $2")
	void set(int index, ItemType value);

	void set(ArrayType array, int offset);

	void set(ItemType[] array, int offset);

	ArrayType subarray(int begin);

	ArrayType subarray(int begin, int end);
}
