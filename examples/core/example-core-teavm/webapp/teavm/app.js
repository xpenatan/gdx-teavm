"use strict";
var main;
(function() {
var $rt_seed = 2463534242;
function $rt_nextId() {
    var x = $rt_seed;
    x ^= x << 13;
    x ^= x >> 17;
    x ^= x << 5;
    $rt_seed = x;
    return x;
}
function $rt_compare(a, b) {
    return a > b ? 1 : a < b ?  -1 : a === b ? 0 : 1;
}
function $rt_isInstance(obj, cls) {
    return obj !== null && !!obj.constructor.$meta && $rt_isAssignable(obj.constructor, cls);
}
function $rt_isAssignable(from, to) {
    if (from === to) {
        return true;
    }
    if (to.$meta.item !== null) {
        return from.$meta.item !== null && $rt_isAssignable(from.$meta.item, to.$meta.item);
    }
    var supertypes = from.$meta.supertypes;
    for (var i = 0;i < supertypes.length;i = i + 1 | 0) {
        if ($rt_isAssignable(supertypes[i], to)) {
            return true;
        }
    }
    return false;
}
function $rt_createArray(cls, sz) {
    var data = new Array(sz);
    var arr = new $rt_array(cls, data);
    if (sz > 0) {
        var i = 0;
        do  {
            data[i] = null;
            i = i + 1 | 0;
        }while (i < sz);
    }
    return arr;
}
function $rt_wrapArray(cls, data) {
    return new $rt_array(cls, data);
}
function $rt_createUnfilledArray(cls, sz) {
    return new $rt_array(cls, new Array(sz));
}
function $rt_createLongArray(sz) {
    var data = new Array(sz);
    var arr = new $rt_array($rt_longcls(), data);
    for (var i = 0;i < sz;i = i + 1 | 0) {
        data[i] = Long_ZERO;
    }
    return arr;
}
function $rt_createNumericArray(cls, nativeArray) {
    return new $rt_array(cls, nativeArray);
}
function $rt_createCharArray(sz) {
    return $rt_createNumericArray($rt_charcls(), new Uint16Array(sz));
}
function $rt_createByteArray(sz) {
    return $rt_createNumericArray($rt_bytecls(), new Int8Array(sz));
}
function $rt_createShortArray(sz) {
    return $rt_createNumericArray($rt_shortcls(), new Int16Array(sz));
}
function $rt_createIntArray(sz) {
    return $rt_createNumericArray($rt_intcls(), new Int32Array(sz));
}
function $rt_createBooleanArray(sz) {
    return $rt_createNumericArray($rt_booleancls(), new Int8Array(sz));
}
function $rt_createFloatArray(sz) {
    return $rt_createNumericArray($rt_floatcls(), new Float32Array(sz));
}
function $rt_createDoubleArray(sz) {
    return $rt_createNumericArray($rt_doublecls(), new Float64Array(sz));
}
function $rt_arraycls(cls) {
    var result = cls.$array;
    if (result === null) {
        var arraycls = {  };
        var name = "[" + cls.$meta.binaryName;
        arraycls.$meta = { item : cls, supertypes : [$rt_objcls()], primitive : false, superclass : $rt_objcls(), name : name, binaryName : name, enum : false };
        arraycls.classObject = null;
        arraycls.$array = null;
        result = arraycls;
        cls.$array = arraycls;
    }
    return result;
}
function $rt_createcls() {
    return { $array : null, classObject : null, $meta : { supertypes : [], superclass : null } };
}
function $rt_createPrimitiveCls(name, binaryName) {
    var cls = $rt_createcls();
    cls.$meta.primitive = true;
    cls.$meta.name = name;
    cls.$meta.binaryName = binaryName;
    cls.$meta.enum = false;
    cls.$meta.item = null;
    return cls;
}
var $rt_booleanclsCache = null;
function $rt_booleancls() {
    if ($rt_booleanclsCache === null) {
        $rt_booleanclsCache = $rt_createPrimitiveCls("boolean", "Z");
    }
    return $rt_booleanclsCache;
}
var $rt_charclsCache = null;
function $rt_charcls() {
    if ($rt_charclsCache === null) {
        $rt_charclsCache = $rt_createPrimitiveCls("char", "C");
    }
    return $rt_charclsCache;
}
var $rt_byteclsCache = null;
function $rt_bytecls() {
    if ($rt_byteclsCache === null) {
        $rt_byteclsCache = $rt_createPrimitiveCls("byte", "B");
    }
    return $rt_byteclsCache;
}
var $rt_shortclsCache = null;
function $rt_shortcls() {
    if ($rt_shortclsCache === null) {
        $rt_shortclsCache = $rt_createPrimitiveCls("short", "S");
    }
    return $rt_shortclsCache;
}
var $rt_intclsCache = null;
function $rt_intcls() {
    if ($rt_intclsCache === null) {
        $rt_intclsCache = $rt_createPrimitiveCls("int", "I");
    }
    return $rt_intclsCache;
}
var $rt_longclsCache = null;
function $rt_longcls() {
    if ($rt_longclsCache === null) {
        $rt_longclsCache = $rt_createPrimitiveCls("long", "J");
    }
    return $rt_longclsCache;
}
var $rt_floatclsCache = null;
function $rt_floatcls() {
    if ($rt_floatclsCache === null) {
        $rt_floatclsCache = $rt_createPrimitiveCls("float", "F");
    }
    return $rt_floatclsCache;
}
var $rt_doubleclsCache = null;
function $rt_doublecls() {
    if ($rt_doubleclsCache === null) {
        $rt_doubleclsCache = $rt_createPrimitiveCls("double", "D");
    }
    return $rt_doubleclsCache;
}
var $rt_voidclsCache = null;
function $rt_voidcls() {
    if ($rt_voidclsCache === null) {
        $rt_voidclsCache = $rt_createPrimitiveCls("void", "V");
    }
    return $rt_voidclsCache;
}
function $rt_throw(ex) {
    throw $rt_exception(ex);
}
function $rt_exception(ex) {
    var err = ex.$jsException;
    if (!err) {
        err = new Error("Java exception thrown");
        if (typeof Error.captureStackTrace === "function") {
            Error.captureStackTrace(err);
        }
        err.$javaException = ex;
        ex.$jsException = err;
        $rt_fillStack(err, ex);
    }
    return err;
}
function $rt_fillStack(err, ex) {
    if (typeof $rt_decodeStack === "function" && err.stack) {
        var stack = $rt_decodeStack(err.stack);
        var javaStack = $rt_createArray($rt_objcls(), stack.length);
        var elem;
        var noStack = false;
        for (var i = 0;i < stack.length;++i) {
            var element = stack[i];
            elem = $rt_createStackElement($rt_str(element.className), $rt_str(element.methodName), $rt_str(element.fileName), element.lineNumber);
            if (elem == null) {
                noStack = true;
                break;
            }
            javaStack.data[i] = elem;
        }
        if (!noStack) {
            $rt_setStack(ex, javaStack);
        }
    }
}
function $rt_createMultiArray(cls, dimensions) {
    var first = 0;
    for (var i = dimensions.length - 1;i >= 0;i = i - 1 | 0) {
        if (dimensions[i] === 0) {
            first = i;
            break;
        }
    }
    if (first > 0) {
        for (i = 0;i < first;i = i + 1 | 0) {
            cls = $rt_arraycls(cls);
        }
        if (first === dimensions.length - 1) {
            return $rt_createArray(cls, dimensions[first]);
        }
    }
    var arrays = new Array($rt_primitiveArrayCount(dimensions, first));
    var firstDim = dimensions[first] | 0;
    for (i = 0;i < arrays.length;i = i + 1 | 0) {
        arrays[i] = $rt_createArray(cls, firstDim);
    }
    return $rt_createMultiArrayImpl(cls, arrays, dimensions, first);
}
function $rt_createByteMultiArray(dimensions) {
    var arrays = new Array($rt_primitiveArrayCount(dimensions, 0));
    if (arrays.length === 0) {
        return $rt_createMultiArray($rt_bytecls(), dimensions);
    }
    var firstDim = dimensions[0] | 0;
    for (var i = 0;i < arrays.length;i = i + 1 | 0) {
        arrays[i] = $rt_createByteArray(firstDim);
    }
    return $rt_createMultiArrayImpl($rt_bytecls(), arrays, dimensions);
}
function $rt_createCharMultiArray(dimensions) {
    var arrays = new Array($rt_primitiveArrayCount(dimensions, 0));
    if (arrays.length === 0) {
        return $rt_createMultiArray($rt_charcls(), dimensions);
    }
    var firstDim = dimensions[0] | 0;
    for (var i = 0;i < arrays.length;i = i + 1 | 0) {
        arrays[i] = $rt_createCharArray(firstDim);
    }
    return $rt_createMultiArrayImpl($rt_charcls(), arrays, dimensions, 0);
}
function $rt_createBooleanMultiArray(dimensions) {
    var arrays = new Array($rt_primitiveArrayCount(dimensions, 0));
    if (arrays.length === 0) {
        return $rt_createMultiArray($rt_booleancls(), dimensions);
    }
    var firstDim = dimensions[0] | 0;
    for (var i = 0;i < arrays.length;i = i + 1 | 0) {
        arrays[i] = $rt_createBooleanArray(firstDim);
    }
    return $rt_createMultiArrayImpl($rt_booleancls(), arrays, dimensions, 0);
}
function $rt_createShortMultiArray(dimensions) {
    var arrays = new Array($rt_primitiveArrayCount(dimensions, 0));
    if (arrays.length === 0) {
        return $rt_createMultiArray($rt_shortcls(), dimensions);
    }
    var firstDim = dimensions[0] | 0;
    for (var i = 0;i < arrays.length;i = i + 1 | 0) {
        arrays[i] = $rt_createShortArray(firstDim);
    }
    return $rt_createMultiArrayImpl($rt_shortcls(), arrays, dimensions, 0);
}
function $rt_createIntMultiArray(dimensions) {
    var arrays = new Array($rt_primitiveArrayCount(dimensions, 0));
    if (arrays.length === 0) {
        return $rt_createMultiArray($rt_intcls(), dimensions);
    }
    var firstDim = dimensions[0] | 0;
    for (var i = 0;i < arrays.length;i = i + 1 | 0) {
        arrays[i] = $rt_createIntArray(firstDim);
    }
    return $rt_createMultiArrayImpl($rt_intcls(), arrays, dimensions, 0);
}
function $rt_createLongMultiArray(dimensions) {
    var arrays = new Array($rt_primitiveArrayCount(dimensions, 0));
    if (arrays.length === 0) {
        return $rt_createMultiArray($rt_longcls(), dimensions);
    }
    var firstDim = dimensions[0] | 0;
    for (var i = 0;i < arrays.length;i = i + 1 | 0) {
        arrays[i] = $rt_createLongArray(firstDim);
    }
    return $rt_createMultiArrayImpl($rt_longcls(), arrays, dimensions, 0);
}
function $rt_createFloatMultiArray(dimensions) {
    var arrays = new Array($rt_primitiveArrayCount(dimensions, 0));
    if (arrays.length === 0) {
        return $rt_createMultiArray($rt_floatcls(), dimensions);
    }
    var firstDim = dimensions[0] | 0;
    for (var i = 0;i < arrays.length;i = i + 1 | 0) {
        arrays[i] = $rt_createFloatArray(firstDim);
    }
    return $rt_createMultiArrayImpl($rt_floatcls(), arrays, dimensions, 0);
}
function $rt_createDoubleMultiArray(dimensions) {
    var arrays = new Array($rt_primitiveArrayCount(dimensions, 0));
    if (arrays.length === 0) {
        return $rt_createMultiArray($rt_doublecls(), dimensions);
    }
    var firstDim = dimensions[0] | 0;
    for (var i = 0;i < arrays.length;i = i + 1 | 0) {
        arrays[i] = $rt_createDoubleArray(firstDim);
    }
    return $rt_createMultiArrayImpl($rt_doublecls(), arrays, dimensions, 0);
}
function $rt_primitiveArrayCount(dimensions, start) {
    var val = dimensions[start + 1] | 0;
    for (var i = start + 2;i < dimensions.length;i = i + 1 | 0) {
        val = val * (dimensions[i] | 0) | 0;
        if (val === 0) {
            break;
        }
    }
    return val;
}
function $rt_createMultiArrayImpl(cls, arrays, dimensions, start) {
    var limit = arrays.length;
    for (var i = start + 1 | 0;i < dimensions.length;i = i + 1 | 0) {
        cls = $rt_arraycls(cls);
        var dim = dimensions[i];
        var index = 0;
        var packedIndex = 0;
        while (index < limit) {
            var arr = $rt_createUnfilledArray(cls, dim);
            for (var j = 0;j < dim;j = j + 1 | 0) {
                arr.data[j] = arrays[index];
                index = index + 1 | 0;
            }
            arrays[packedIndex] = arr;
            packedIndex = packedIndex + 1 | 0;
        }
        limit = packedIndex;
    }
    return arrays[0];
}
function $rt_assertNotNaN(value) {
    if (typeof value === 'number' && isNaN(value)) {
        throw "NaN";
    }
    return value;
}
var $rt_stdoutBuffer = "";
var $rt_putStdout = typeof $rt_putStdoutCustom === "function" ? $rt_putStdoutCustom : function(ch) {
    if (ch === 0xA) {
        if (console) {
            console.info($rt_stdoutBuffer);
        }
        $rt_stdoutBuffer = "";
    } else {
        $rt_stdoutBuffer += String.fromCharCode(ch);
    }
};
var $rt_stderrBuffer = "";
var $rt_putStderr = typeof $rt_putStderrCustom === "function" ? $rt_putStderrCustom : function(ch) {
    if (ch === 0xA) {
        if (console) {
            console.error($rt_stderrBuffer);
        }
        $rt_stderrBuffer = "";
    } else {
        $rt_stderrBuffer += String.fromCharCode(ch);
    }
};
var $rt_packageData = null;
function $rt_packages(data) {
    var i = 0;
    var packages = new Array(data.length);
    for (var j = 0;j < data.length;++j) {
        var prefixIndex = data[i++];
        var prefix = prefixIndex >= 0 ? packages[prefixIndex] : "";
        packages[j] = prefix + data[i++] + ".";
    }
    $rt_packageData = packages;
}
function $rt_metadata(data) {
    var packages = $rt_packageData;
    var i = 0;
    while (i < data.length) {
        var cls = data[i++];
        cls.$meta = {  };
        var m = cls.$meta;
        var className = data[i++];
        m.name = className !== 0 ? className : null;
        if (m.name !== null) {
            var packageIndex = data[i++];
            if (packageIndex >= 0) {
                m.name = packages[packageIndex] + m.name;
            }
        }
        m.binaryName = "L" + m.name + ";";
        var superclass = data[i++];
        m.superclass = superclass !== 0 ? superclass : null;
        m.supertypes = data[i++];
        if (m.superclass) {
            m.supertypes.push(m.superclass);
            cls.prototype = Object.create(m.superclass.prototype);
        } else {
            cls.prototype = {  };
        }
        var flags = data[i++];
        m.enum = (flags & 8) !== 0;
        m.flags = flags;
        m.primitive = false;
        m.item = null;
        cls.prototype.constructor = cls;
        cls.classObject = null;
        m.accessLevel = data[i++];
        var clinit = data[i++];
        cls.$clinit = clinit !== 0 ? clinit : function() {
        };
        var virtualMethods = data[i++];
        if (virtualMethods !== 0) {
            for (var j = 0;j < virtualMethods.length;j += 2) {
                var name = virtualMethods[j];
                var func = virtualMethods[j + 1];
                if (typeof name === 'string') {
                    name = [name];
                }
                for (var k = 0;k < name.length;++k) {
                    cls.prototype[name[k]] = func;
                }
            }
        }
        cls.$array = null;
    }
}
function $rt_threadStarter(f) {
    return function() {
        var args = Array.prototype.slice.apply(arguments);
        $rt_startThread(function() {
            f.apply(this, args);
        });
    };
}
function $rt_mainStarter(f) {
    return function(args, callback) {
        if (!args) {
            args = [];
        }
        var javaArgs = $rt_createArray($rt_objcls(), args.length);
        for (var i = 0;i < args.length;++i) {
            javaArgs.data[i] = $rt_str(args[i]);
        }
        $rt_startThread(function() {
            f.call(null, javaArgs);
        }, callback);
    };
}
var $rt_stringPool_instance;
function $rt_stringPool(strings) {
    $rt_stringPool_instance = new Array(strings.length);
    for (var i = 0;i < strings.length;++i) {
        $rt_stringPool_instance[i] = $rt_intern($rt_str(strings[i]));
    }
}
function $rt_s(index) {
    return $rt_stringPool_instance[index];
}
function $rt_eraseClinit(target) {
    return target.$clinit = function() {
    };
}
var $rt_numberConversionView = new DataView(new ArrayBuffer(8));
function $rt_doubleToLongBits(n) {
    $rt_numberConversionView.setFloat64(0, n, true);
    return new Long($rt_numberConversionView.getInt32(0, true), $rt_numberConversionView.getInt32(4, true));
}
function $rt_longBitsToDouble(n) {
    $rt_numberConversionView.setInt32(0, n.lo, true);
    $rt_numberConversionView.setInt32(4, n.hi, true);
    return $rt_numberConversionView.getFloat64(0, true);
}
function $rt_floatToIntBits(n) {
    $rt_numberConversionView.setFloat32(0, n);
    return $rt_numberConversionView.getInt32(0);
}
function $rt_intBitsToFloat(n) {
    $rt_numberConversionView.setInt32(0, n);
    return $rt_numberConversionView.getFloat32(0);
}
function $rt_javaException(e) {
    return e instanceof Error && typeof e.$javaException === 'object' ? e.$javaException : null;
}
function $rt_jsException(e) {
    return typeof e.$jsException === 'object' ? e.$jsException : null;
}
function $rt_wrapException(err) {
    var ex = err.$javaException;
    if (!ex) {
        ex = $rt_createException($rt_str("(JavaScript) " + err.toString()));
        err.$javaException = ex;
        ex.$jsException = err;
        $rt_fillStack(err, ex);
    }
    return ex;
}
function $dbg_class(obj) {
    var cls = obj.constructor;
    var arrayDegree = 0;
    while (cls.$meta && cls.$meta.item) {
        ++arrayDegree;
        cls = cls.$meta.item;
    }
    var clsName = "";
    if (cls === $rt_booleancls()) {
        clsName = "boolean";
    } else if (cls === $rt_bytecls()) {
        clsName = "byte";
    } else if (cls === $rt_shortcls()) {
        clsName = "short";
    } else if (cls === $rt_charcls()) {
        clsName = "char";
    } else if (cls === $rt_intcls()) {
        clsName = "int";
    } else if (cls === $rt_longcls()) {
        clsName = "long";
    } else if (cls === $rt_floatcls()) {
        clsName = "float";
    } else if (cls === $rt_doublecls()) {
        clsName = "double";
    } else {
        clsName = cls.$meta ? cls.$meta.name || "a/" + cls.name : "@" + cls.name;
    }
    while (arrayDegree-- > 0) {
        clsName += "[]";
    }
    return clsName;
}
function Long(lo, hi) {
    this.lo = lo | 0;
    this.hi = hi | 0;
}
Long.prototype.__teavm_class__ = function() {
    return "long";
};
Long.prototype.toString = function() {
    var result = [];
    var n = this;
    var positive = Long_isPositive(n);
    if (!positive) {
        n = Long_neg(n);
    }
    var radix = new Long(10, 0);
    do  {
        var divRem = Long_divRem(n, radix);
        result.push(String.fromCharCode(48 + divRem[1].lo));
        n = divRem[0];
    }while (n.lo !== 0 || n.hi !== 0);
    result = (result.reverse()).join('');
    return positive ? result : "-" + result;
};
Long.prototype.valueOf = function() {
    return Long_toNumber(this);
};
var Long_ZERO = new Long(0, 0);
var Long_MAX_NORMAL = 1 << 18;
function Long_fromInt(val) {
    return val >= 0 ? new Long(val, 0) : new Long(val,  -1);
}
function Long_fromNumber(val) {
    if (val >= 0) {
        return new Long(val | 0, val / 0x100000000 | 0);
    } else {
        return Long_neg(new Long( -val | 0,  -val / 0x100000000 | 0));
    }
}
function Long_toNumber(val) {
    var lo = val.lo;
    var hi = val.hi;
    if (lo < 0) {
        lo += 0x100000000;
    }
    return 0x100000000 * hi + lo;
}
var $rt_imul = Math.imul || function(a, b) {
    var ah = a >>> 16 & 0xFFFF;
    var al = a & 0xFFFF;
    var bh = b >>> 16 & 0xFFFF;
    var bl = b & 0xFFFF;
    return al * bl + (ah * bl + al * bh << 16 >>> 0) | 0;
};
var $rt_udiv = function(a, b) {
    if (a < 0) {
        a += 0x100000000;
    }
    if (b < 0) {
        b += 0x100000000;
    }
    return a / b | 0;
};
var $rt_umod = function(a, b) {
    if (a < 0) {
        a += 0x100000000;
    }
    if (b < 0) {
        b += 0x100000000;
    }
    return a % b | 0;
};
function $rt_setCloneMethod(target, f) {
    target.$clone = f;
}
function $rt_cls(cls) {
    return jl_Class_getClass(cls);
}
function $rt_str(str) {
    if (str === null) {
        return null;
    }
    var characters = $rt_createCharArray(str.length);
    var charsBuffer = characters.data;
    for (var i = 0; i < str.length; i = (i + 1) | 0) {
        charsBuffer[i] = str.charCodeAt(i) & 0xFFFF;
    }
    return jl_String__init_(characters);
}
function $rt_ustr(str) {
    if (str === null) {
        return null;
    }
    var data = str.$characters.data;
    var result = "";
    for (var i = 0; i < data.length; i = (i + 1) | 0) {
        result += String.fromCharCode(data[i]);
    }
    return result;
}
function $rt_objcls() { return jl_Object; }
function $rt_nullCheck(val) {
    if (val === null) {
        $rt_throw(jl_NullPointerException__init_());
    }
    return val;
}
function $rt_intern(str) {
    return str;
}
function $rt_getThread() {
    return null;
}
function $rt_setThread(t) {
}
function $rt_createException(message) {
    return jl_RuntimeException__init_(message);
}
function $rt_createStackElement(className, methodName, fileName, lineNumber) {
    return null;
}
function $rt_setStack(e, stack) {
}
var $java = Object.create(null);
function jl_Object() {
    this.$id$ = 0;
}
function jl_Object__init_() {
    var var_0 = new jl_Object();
    jl_Object__init_0(var_0);
    return var_0;
}
function jl_Object__init_0($this) {
    return;
}
function jl_Object_getClass($this) {
    return jl_Class_getClass($this.constructor);
}
function jl_Object_toString($this) {
    return jl_StringBuilder__init_().$append(jl_Object_getClass($this).$getName()).$append($rt_s(0)).$append(jl_Integer_toHexString(jl_Object_identity($this))).$toString();
}
function jl_Object_identity($this) {
    var $platformThis, var$2;
    $platformThis = $this;
    if (!$platformThis.$id$) {
        var$2 = $rt_nextId();
        $platformThis.$id$ = var$2;
    }
    return $this.$id$;
}
function jl_Throwable() {
    var a = this; jl_Object.call(a);
    a.$message = null;
    a.$suppressionEnabled = 0;
    a.$writableStackTrace = 0;
}
function jl_Throwable__init_() {
    var var_0 = new jl_Throwable();
    jl_Throwable__init_0(var_0);
    return var_0;
}
function jl_Throwable__init_1(var_0) {
    var var_1 = new jl_Throwable();
    jl_Throwable__init_2(var_1, var_0);
    return var_1;
}
function jl_Throwable__init_0($this) {
    $this.$suppressionEnabled = 1;
    $this.$writableStackTrace = 1;
    $this.$fillInStackTrace();
}
function jl_Throwable__init_2($this, $message) {
    $this.$suppressionEnabled = 1;
    $this.$writableStackTrace = 1;
    $this.$fillInStackTrace();
    $this.$message = $message;
}
function jl_Throwable_fillInStackTrace($this) {
    return $this;
}
function jl_Exception() {
    jl_Throwable.call(this);
}
function jl_Exception__init_() {
    var var_0 = new jl_Exception();
    jl_Exception__init_0(var_0);
    return var_0;
}
function jl_Exception__init_1(var_0) {
    var var_1 = new jl_Exception();
    jl_Exception__init_2(var_1, var_0);
    return var_1;
}
function jl_Exception__init_0($this) {
    jl_Throwable__init_0($this);
}
function jl_Exception__init_2($this, $message) {
    jl_Throwable__init_2($this, $message);
}
function jl_RuntimeException() {
    jl_Exception.call(this);
}
function jl_RuntimeException__init_0() {
    var var_0 = new jl_RuntimeException();
    jl_RuntimeException__init_1(var_0);
    return var_0;
}
function jl_RuntimeException__init_(var_0) {
    var var_1 = new jl_RuntimeException();
    jl_RuntimeException__init_2(var_1, var_0);
    return var_1;
}
function jl_RuntimeException__init_1($this) {
    jl_Exception__init_0($this);
}
function jl_RuntimeException__init_2($this, $message) {
    jl_Exception__init_2($this, $message);
}
function jl_IndexOutOfBoundsException() {
    jl_RuntimeException.call(this);
}
function jl_IndexOutOfBoundsException__init_() {
    var var_0 = new jl_IndexOutOfBoundsException();
    jl_IndexOutOfBoundsException__init_0(var_0);
    return var_0;
}
function jl_IndexOutOfBoundsException__init_0($this) {
    jl_RuntimeException__init_1($this);
}
function ju_Arrays() {
    jl_Object.call(this);
}
function ju_Arrays_copyOf($array, $length) {
    var var$3, $result, $sz, $i;
    var$3 = $array.data;
    $result = $rt_createCharArray($length);
    $sz = jl_Math_min($length, var$3.length);
    $i = 0;
    while ($i < $sz) {
        $result.data[$i] = var$3[$i];
        $i = $i + 1 | 0;
    }
    return $result;
}
function cbg_ApplicationListener() {
}
function cbgtde_GearsDemo() {
    var a = this; jl_Object.call(a);
    a.$transform = null;
    a.$center = null;
    a.$transformedCenter = null;
}
function cbgtde_GearsDemo__init_() {
    var var_0 = new cbgtde_GearsDemo();
    cbgtde_GearsDemo__init_0(var_0);
    return var_0;
}
function cbgtde_GearsDemo__init_0($this) {
    jl_Object__init_0($this);
    $this.$transform = cbgm_Matrix4__init_();
    $this.$center = cbgm_Vector3__init_();
    $this.$transformedCenter = cbgm_Vector3__init_();
}
function cbgg_GL20() {
}
function ji_Serializable() {
}
function jl_Number() {
    jl_Object.call(this);
}
function jl_Comparable() {
}
function jl_Integer() {
    jl_Number.call(this);
}
var jl_Integer_TYPE = null;
function jl_Integer_$callClinit() {
    jl_Integer_$callClinit = $rt_eraseClinit(jl_Integer);
    jl_Integer__clinit_();
}
function jl_Integer_toHexString($i) {
    jl_Integer_$callClinit();
    return otci_IntegerUtil_toUnsignedLogRadixString($i, 4);
}
function jl_Integer_numberOfLeadingZeros($i) {
    var $n, var$3, var$4;
    jl_Integer_$callClinit();
    if (!$i)
        return 32;
    $n = 0;
    var$3 = $i >>> 16;
    if (var$3)
        $n = 16;
    else
        var$3 = $i;
    var$4 = var$3 >>> 8;
    if (!var$4)
        var$4 = var$3;
    else
        $n = $n | 8;
    var$3 = var$4 >>> 4;
    if (!var$3)
        var$3 = var$4;
    else
        $n = $n | 4;
    var$4 = var$3 >>> 2;
    if (!var$4)
        var$4 = var$3;
    else
        $n = $n | 2;
    if (var$4 >>> 1)
        $n = $n | 1;
    return (32 - $n | 0) - 1 | 0;
}
function jl_Integer__clinit_() {
    jl_Integer_TYPE = $rt_cls($rt_intcls());
}
function cgxgbwd_ElementWrapper() {
}
function cgxgbwd_HTMLCanvasElementWrapper() {
}
function cgxgbtd_TeaCanvas() {
    jl_Object.call(this);
    this.$canvas = null;
}
function cgxgbtd_TeaCanvas__init_(var_0) {
    var var_1 = new cgxgbtd_TeaCanvas();
    cgxgbtd_TeaCanvas__init_0(var_1, var_0);
    return var_1;
}
function cgxgbtd_TeaCanvas__init_0($this, $canvas) {
    jl_Object__init_0($this);
    $this.$canvas = $canvas;
}
function cgxgbtd_TeaCanvas_getWidth($this) {
    return $this.$canvas.width;
}
function cgxgbtd_TeaCanvas_getHeight($this) {
    return $this.$canvas.height;
}
function cgxgbtd_TeaCanvas_getGLContext($this, $config) {
    var $attr, var$3, $context;
    $attr = otjw_WebGLContextAttributes_create$js_body$_13();
    var$3 = !!$config.$alpha;
    $attr.alpha = var$3;
    var$3 = !!$config.$antialiasing;
    $attr.antialias = var$3;
    var$3 = !!$config.$stencil;
    $attr.stencil = var$3;
    var$3 = !!$config.$premultipliedAlpha;
    $attr.premultipliedAlpha = var$3;
    var$3 = !!$config.$preserveDrawingBuffer;
    $attr.preserveDrawingBuffer = var$3;
    $context = $this.$canvas.getContext("webgl", $attr);
    return cgxgbt_TeaGLContext__init_($context);
}
function cgxgbtd_TeaCanvas_getGLContext0(var$0, var$1) {
    return var$0.$getGLContext(var$1);
}
function cgxgbwg_WebGLRenderingContextWrapper() {
}
function cgxgbt_TeaGLContext() {
    jl_Object.call(this);
    this.$context = null;
}
function cgxgbt_TeaGLContext__init_(var_0) {
    var var_1 = new cgxgbt_TeaGLContext();
    cgxgbt_TeaGLContext__init_0(var_1, var_0);
    return var_1;
}
function cgxgbt_TeaGLContext__init_0($this, $context) {
    jl_Object__init_0($this);
    $this.$context = $context;
}
function cgxgbt_TeaGLContext_viewport($this, $x, $y, $width, $height) {
    $this.$context.viewport($x, $y, $width, $height);
}
function cgxgbt_TeaGLContext_clear($this, $mask) {
    $this.$context.clear($mask);
}
function cgxgbt_TeaGLContext_clearColor($this, $red, $green, $blue, $alpha) {
    $this.$context.clearColor($red, $green, $blue, $alpha);
}
function jl_Error() {
    jl_Throwable.call(this);
}
function jl_Error__init_(var_0) {
    var var_1 = new jl_Error();
    jl_Error__init_0(var_1, var_0);
    return var_1;
}
function jl_Error__init_0($this, $message) {
    jl_Throwable__init_2($this, $message);
}
function jl_LinkageError() {
    jl_Error.call(this);
}
function jl_LinkageError__init_(var_0) {
    var var_1 = new jl_LinkageError();
    jl_LinkageError__init_0(var_1, var_0);
    return var_1;
}
function jl_LinkageError__init_0($this, $message) {
    jl_Error__init_0($this, $message);
}
function jl_IncompatibleClassChangeError() {
    jl_LinkageError.call(this);
}
function jl_IncompatibleClassChangeError__init_(var_0) {
    var var_1 = new jl_IncompatibleClassChangeError();
    jl_IncompatibleClassChangeError__init_0(var_1, var_0);
    return var_1;
}
function jl_IncompatibleClassChangeError__init_0($this, $message) {
    jl_LinkageError__init_0($this, $message);
}
function jl_NoSuchFieldError() {
    jl_IncompatibleClassChangeError.call(this);
}
function jl_NoSuchFieldError__init_(var_0) {
    var var_1 = new jl_NoSuchFieldError();
    jl_NoSuchFieldError__init_0(var_1, var_0);
    return var_1;
}
function jl_NoSuchFieldError__init_0($this, $message) {
    jl_IncompatibleClassChangeError__init_0($this, $message);
}
function cgxgbw_WebApplicationConfiguration() {
    var a = this; jl_Object.call(a);
    a.$canvas0 = null;
    a.$stencil = 0;
    a.$antialiasing = 0;
    a.$alpha = 0;
    a.$premultipliedAlpha = 0;
    a.$preserveDrawingBuffer = 0;
    a.$useDebugGL = 0;
    a.$preferFlash = 0;
}
function cgxgbw_WebApplicationConfiguration__init_() {
    var var_0 = new cgxgbw_WebApplicationConfiguration();
    cgxgbw_WebApplicationConfiguration__init_0(var_0);
    return var_0;
}
function cgxgbw_WebApplicationConfiguration__init_0($this) {
    jl_Object__init_0($this);
    $this.$stencil = 0;
    $this.$antialiasing = 0;
    $this.$alpha = 0;
    $this.$premultipliedAlpha = 0;
    $this.$preserveDrawingBuffer = 0;
    $this.$useDebugGL = 0;
    $this.$preferFlash = 0;
}
function jl_Character() {
    jl_Object.call(this);
}
var jl_Character_TYPE = null;
var jl_Character_characterCache = null;
function jl_Character_$callClinit() {
    jl_Character_$callClinit = $rt_eraseClinit(jl_Character);
    jl_Character__clinit_();
}
function jl_Character_forDigit($digit, $radix) {
    jl_Character_$callClinit();
    if ($radix >= 2 && $radix <= 36 && $digit < $radix)
        return $digit < 10 ? (48 + $digit | 0) & 65535 : ((97 + $digit | 0) - 10 | 0) & 65535;
    return 0;
}
function jl_Character__clinit_() {
    jl_Character_TYPE = $rt_cls($rt_charcls());
    jl_Character_characterCache = $rt_createArray(jl_Character, 128);
}
function cbgm_Vector() {
}
function cbgm_Vector3() {
    var a = this; jl_Object.call(a);
    a.$x = 0.0;
    a.$y = 0.0;
    a.$z = 0.0;
}
var cbgm_Vector3_X = null;
var cbgm_Vector3_Y = null;
var cbgm_Vector3_Z = null;
var cbgm_Vector3_Zero = null;
var cbgm_Vector3_tmpMat = null;
function cbgm_Vector3_$callClinit() {
    cbgm_Vector3_$callClinit = $rt_eraseClinit(cbgm_Vector3);
    cbgm_Vector3__clinit_();
}
function cbgm_Vector3__init_() {
    var var_0 = new cbgm_Vector3();
    cbgm_Vector3__init_0(var_0);
    return var_0;
}
function cbgm_Vector3__init_1(var_0, var_1, var_2) {
    var var_3 = new cbgm_Vector3();
    cbgm_Vector3__init_2(var_3, var_0, var_1, var_2);
    return var_3;
}
function cbgm_Vector3__clinit_() {
    cbgm_Vector3_X = cbgm_Vector3__init_1(1.0, 0.0, 0.0);
    cbgm_Vector3_Y = cbgm_Vector3__init_1(0.0, 1.0, 0.0);
    cbgm_Vector3_Z = cbgm_Vector3__init_1(0.0, 0.0, 1.0);
    cbgm_Vector3_Zero = cbgm_Vector3__init_1(0.0, 0.0, 0.0);
    cbgm_Vector3_tmpMat = cbgm_Matrix4__init_();
}
function cbgm_Vector3__init_0($this) {
    cbgm_Vector3_$callClinit();
    jl_Object__init_0($this);
}
function cbgm_Vector3__init_2($this, $x, $y, $z) {
    cbgm_Vector3_$callClinit();
    jl_Object__init_0($this);
    $this.$set($x, $y, $z);
}
function cbgm_Vector3_set($this, $x, $y, $z) {
    $this.$x = $x;
    $this.$y = $y;
    $this.$z = $z;
    return $this;
}
function otci_IntegerUtil() {
    jl_Object.call(this);
}
function otci_IntegerUtil_toUnsignedLogRadixString($value, $radixLog2) {
    var $radix, $mask, $sz, $chars, $pos, $target, var$9, $target_0;
    if (!$value)
        return $rt_s(1);
    $radix = 1 << $radixLog2;
    $mask = $radix - 1 | 0;
    $sz = (((32 - jl_Integer_numberOfLeadingZeros($value) | 0) + $radixLog2 | 0) - 1 | 0) / $radixLog2 | 0;
    $chars = $rt_createCharArray($sz);
    $pos = $rt_imul($sz - 1 | 0, $radixLog2);
    $target = 0;
    while ($pos >= 0) {
        var$9 = $chars.data;
        $target_0 = $target + 1 | 0;
        var$9[$target] = jl_Character_forDigit($value >>> $pos & $mask, $radix);
        $pos = $pos - $radixLog2 | 0;
        $target = $target_0;
    }
    return jl_String__init_($chars);
}
function jl_Math() {
    jl_Object.call(this);
}
function jl_Math_min($a, $b) {
    if ($a < $b)
        $b = $a;
    return $b;
}
function jl_Math_max($a, $b) {
    if ($a > $b)
        $b = $a;
    return $b;
}
function cgxgbw_WebGL20() {
    jl_Object.call(this);
    this.$gl = null;
}
function cgxgbw_WebGL20__init_(var_0) {
    var var_1 = new cgxgbw_WebGL20();
    cgxgbw_WebGL20__init_0(var_1, var_0);
    return var_1;
}
function cgxgbw_WebGL20__init_0($this, $gl) {
    jl_Object__init_0($this);
    $this.$gl = $gl;
}
function cgxgbw_WebGL20_glClear($this, $mask) {
    $this.$gl.$clear($mask);
}
function cgxgbw_WebGL20_glClearColor($this, $red, $green, $blue, $alpha) {
    $this.$gl.$clearColor($red, $green, $blue, $alpha);
}
function cgxgbw_WebGL20_glViewport($this, $x, $y, $width, $height) {
    $this.$gl.$viewport($x, $y, $width, $height);
}
function otji_JS() {
    jl_Object.call(this);
}
function otji_JS_functionAsObject(var$1, var$2) {
    if (typeof var$1 !== "function") return var$1;
    var result = {};
    result[var$2] = var$1;
    return result;
}
function cgxgbwd_WindowWrapper() {
}
function jl_CharSequence() {
}
function cgxgbt_TeaApplicationConfiguration() {
    cgxgbw_WebApplicationConfiguration.call(this);
}
function cgxgbt_TeaApplicationConfiguration__init_(var_0) {
    var var_1 = new cgxgbt_TeaApplicationConfiguration();
    cgxgbt_TeaApplicationConfiguration__init_0(var_1, var_0);
    return var_1;
}
function cgxgbt_TeaApplicationConfiguration__init_0($this, $canvasID) {
    var $window, $document;
    cgxgbw_WebApplicationConfiguration__init_0($this);
    $window = cgxgbtd_TeaWindow_getCurrent();
    $document = $window.$getDocument();
    $this.$canvas0 = $document.$getCanvas($canvasID);
}
function otj_JSObject() {
}
function otjde_EventTarget() {
}
function otjde_LoadEventTarget() {
}
function cbg_Graphics() {
}
function jl_StringIndexOutOfBoundsException() {
    jl_IndexOutOfBoundsException.call(this);
}
function jl_StringIndexOutOfBoundsException__init_() {
    var var_0 = new jl_StringIndexOutOfBoundsException();
    jl_StringIndexOutOfBoundsException__init_0(var_0);
    return var_0;
}
function jl_StringIndexOutOfBoundsException__init_0($this) {
    jl_IndexOutOfBoundsException__init_0($this);
}
function cbgm_Quaternion() {
    var a = this; jl_Object.call(a);
    a.$x0 = 0.0;
    a.$y0 = 0.0;
    a.$z0 = 0.0;
    a.$w = 0.0;
}
var cbgm_Quaternion_tmp1 = null;
var cbgm_Quaternion_tmp2 = null;
function cbgm_Quaternion_$callClinit() {
    cbgm_Quaternion_$callClinit = $rt_eraseClinit(cbgm_Quaternion);
    cbgm_Quaternion__clinit_();
}
function cbgm_Quaternion__init_(var_0, var_1, var_2, var_3) {
    var var_4 = new cbgm_Quaternion();
    cbgm_Quaternion__init_0(var_4, var_0, var_1, var_2, var_3);
    return var_4;
}
function cbgm_Quaternion__init_1() {
    var var_0 = new cbgm_Quaternion();
    cbgm_Quaternion__init_2(var_0);
    return var_0;
}
function cbgm_Quaternion__clinit_() {
    cbgm_Quaternion_tmp1 = cbgm_Quaternion__init_(0.0, 0.0, 0.0, 0.0);
    cbgm_Quaternion_tmp2 = cbgm_Quaternion__init_(0.0, 0.0, 0.0, 0.0);
}
function cbgm_Quaternion__init_0($this, $x, $y, $z, $w) {
    cbgm_Quaternion_$callClinit();
    jl_Object__init_0($this);
    $this.$set0($x, $y, $z, $w);
}
function cbgm_Quaternion__init_2($this) {
    cbgm_Quaternion_$callClinit();
    jl_Object__init_0($this);
    $this.$idt();
}
function cbgm_Quaternion_set($this, $x, $y, $z, $w) {
    $this.$x0 = $x;
    $this.$y0 = $y;
    $this.$z0 = $z;
    $this.$w = $w;
    return $this;
}
function cbgm_Quaternion_idt($this) {
    return $this.$set0(0.0, 0.0, 0.0, 1.0);
}
function cgxgbwd_HTMLDocumentWrapper() {
}
function cgxgbtd_TeaDocument() {
    jl_Object.call(this);
    this.$document = null;
}
function cgxgbtd_TeaDocument__init_(var_0) {
    var var_1 = new cgxgbtd_TeaDocument();
    cgxgbtd_TeaDocument__init_0(var_1, var_0);
    return var_1;
}
function cgxgbtd_TeaDocument__init_0($this, $document) {
    jl_Object__init_0($this);
    $this.$document = $document;
}
function cgxgbtd_TeaDocument_getCanvas($this, $id) {
    var $canvas;
    $canvas = $this.$document.getElementById($rt_ustr($id));
    return cgxgbtd_TeaCanvas__init_($canvas);
}
function ju_Comparator() {
}
function jl_String$_clinit_$lambda$_81_0() {
    jl_Object.call(this);
}
function jl_String$_clinit_$lambda$_81_0__init_() {
    var var_0 = new jl_String$_clinit_$lambda$_81_0();
    jl_String$_clinit_$lambda$_81_0__init_0(var_0);
    return var_0;
}
function jl_String$_clinit_$lambda$_81_0__init_0(var$0) {
    jl_Object__init_0(var$0);
}
function jl_AbstractStringBuilder() {
    var a = this; jl_Object.call(a);
    a.$buffer = null;
    a.$length = 0;
}
function jl_AbstractStringBuilder__init_() {
    var var_0 = new jl_AbstractStringBuilder();
    jl_AbstractStringBuilder__init_0(var_0);
    return var_0;
}
function jl_AbstractStringBuilder__init_1(var_0) {
    var var_1 = new jl_AbstractStringBuilder();
    jl_AbstractStringBuilder__init_2(var_1, var_0);
    return var_1;
}
function jl_AbstractStringBuilder__init_0($this) {
    jl_AbstractStringBuilder__init_2($this, 16);
}
function jl_AbstractStringBuilder__init_2($this, $capacity) {
    jl_Object__init_0($this);
    $this.$buffer = $rt_createCharArray($capacity);
}
function jl_AbstractStringBuilder_append($this, $string) {
    return $this.$insert($this.$length, $string);
}
function jl_AbstractStringBuilder_insert($this, $index, $string) {
    var $i, var$4, var$5;
    if ($index >= 0 && $index <= $this.$length) {
        if ($string === null)
            $string = $rt_s(2);
        else if ($string.$isEmpty())
            return $this;
        $this.$ensureCapacity($this.$length + $string.$length0() | 0);
        $i = $this.$length - 1 | 0;
        while ($i >= $index) {
            $this.$buffer.data[$i + $string.$length0() | 0] = $this.$buffer.data[$i];
            $i = $i + (-1) | 0;
        }
        $this.$length = $this.$length + $string.$length0() | 0;
        $i = 0;
        while ($i < $string.$length0()) {
            var$4 = $this.$buffer.data;
            var$5 = $index + 1 | 0;
            var$4[$index] = $string.$charAt($i);
            $i = $i + 1 | 0;
            $index = var$5;
        }
        return $this;
    }
    $rt_throw(jl_StringIndexOutOfBoundsException__init_());
}
function jl_AbstractStringBuilder_ensureCapacity($this, $capacity) {
    var $newLength, var$3;
    if ($this.$buffer.data.length >= $capacity)
        return;
    if ($this.$buffer.data.length >= 1073741823)
        $newLength = 2147483647;
    else {
        var$3 = $this.$buffer.data.length * 2 | 0;
        $newLength = jl_Math_max($capacity, jl_Math_max(var$3, 5));
    }
    $this.$buffer = ju_Arrays_copyOf($this.$buffer, $newLength);
}
function jl_AbstractStringBuilder_toString($this) {
    return jl_String__init_0($this.$buffer, 0, $this.$length);
}
function jl_Appendable() {
}
function jl_StringBuilder() {
    jl_AbstractStringBuilder.call(this);
}
function jl_StringBuilder__init_() {
    var var_0 = new jl_StringBuilder();
    jl_StringBuilder__init_0(var_0);
    return var_0;
}
function jl_StringBuilder__init_0($this) {
    jl_AbstractStringBuilder__init_0($this);
}
function jl_StringBuilder_append($this, $string) {
    jl_AbstractStringBuilder_append($this, $string);
    return $this;
}
function jl_StringBuilder_insert($this, $index, $string) {
    jl_AbstractStringBuilder_insert($this, $index, $string);
    return $this;
}
function jl_StringBuilder_toString($this) {
    return jl_AbstractStringBuilder_toString($this);
}
function jl_StringBuilder_ensureCapacity($this, var$1) {
    jl_AbstractStringBuilder_ensureCapacity($this, var$1);
}
function jl_StringBuilder_insert0($this, var$1, var$2) {
    return $this.$insert0(var$1, var$2);
}
function jlr_AnnotatedElement() {
}
function cbg_Application() {
}
function cgxgbw_WebApplication() {
    var a = this; jl_Object.call(a);
    a.$graphics = null;
    a.$canvas1 = null;
    a.$config = null;
    a.$appListener = null;
}
function cgxgbw_WebApplication__init_(var_0, var_1) {
    var var_2 = new cgxgbw_WebApplication();
    cgxgbw_WebApplication__init_0(var_2, var_0, var_1);
    return var_2;
}
function cgxgbw_WebApplication__init_0($this, $appListener, $config) {
    jl_Object__init_0($this);
    $this.$appListener = $appListener;
    $this.$config = $config;
    $this.$canvas1 = $config.$canvas0;
    cgxgbw_WebApplication_init($this);
}
function cgxgbw_WebApplication_init($this) {
    $this.$graphics = cgxgbw_WebGraphics__init_($this.$config);
    cbg_Gdx_app = $this;
    cbg_Gdx_graphics = $this.$graphics;
    cbg_Gdx_gl = $this.$graphics.$getGL20();
    cbg_Gdx_gl20 = $this.$graphics.$getGL20();
}
function cgxgbte_Launcher() {
    jl_Object.call(this);
}
function cgxgbte_Launcher__init_() {
    var var_0 = new cgxgbte_Launcher();
    cgxgbte_Launcher__init_0(var_0);
    return var_0;
}
function cgxgbte_Launcher__init_0($this) {
    jl_Object__init_0($this);
}
function cgxgbte_Launcher_main($args) {
    cgxgbte_Launcher__init_().$build();
}
function cgxgbte_Launcher_build($this) {
    var $config, $gearsDemo;
    $config = cgxgbt_TeaApplicationConfiguration__init_($rt_s(3));
    $gearsDemo = cbgtde_GearsDemo__init_();
    cgxgbw_WebApplication__init_0(new cgxgbw_WebApplication, $gearsDemo, $config);
}
function otjde_FocusEventTarget() {
}
function otjde_MouseEventTarget() {
}
function otjde_KeyboardEventTarget() {
}
function otjb_WindowEventTarget() {
}
function cbg_Gdx() {
    jl_Object.call(this);
}
var cbg_Gdx_app = null;
var cbg_Gdx_graphics = null;
var cbg_Gdx_gl = null;
var cbg_Gdx_gl20 = null;
function otjb_StorageProvider() {
}
function otjc_JSArrayReader() {
}
function otjb_Window() {
    jl_Object.call(this);
}
function otjb_Window_addEventListener$exported$0(var$0, var$1, var$2) {
    var$0.$addEventListener($rt_str(var$1), otji_JS_functionAsObject(var$2, "handleEvent"));
}
function otjb_Window_removeEventListener$exported$1(var$0, var$1, var$2) {
    var$0.$removeEventListener($rt_str(var$1), otji_JS_functionAsObject(var$2, "handleEvent"));
}
function otjb_Window_get$exported$2(var$0, var$1) {
    return var$0.$get(var$1);
}
function otjb_Window_removeEventListener$exported$3(var$0, var$1, var$2, var$3) {
    var$0.$removeEventListener0($rt_str(var$1), otji_JS_functionAsObject(var$2, "handleEvent"), var$3 ? 1 : 0);
}
function otjb_Window_dispatchEvent$exported$4(var$0, var$1) {
    return !!var$0.$dispatchEvent(var$1);
}
function otjb_Window_getLength$exported$5(var$0) {
    return var$0.$getLength();
}
function otjb_Window_addEventListener$exported$6(var$0, var$1, var$2, var$3) {
    var$0.$addEventListener0($rt_str(var$1), otji_JS_functionAsObject(var$2, "handleEvent"), var$3 ? 1 : 0);
}
function otp_Platform() {
    jl_Object.call(this);
}
function otp_Platform_getName($cls) {
    return $rt_str($cls.$meta.name);
}
function jl_String() {
    var a = this; jl_Object.call(a);
    a.$characters = null;
    a.$hashCode = 0;
}
var jl_String_CASE_INSENSITIVE_ORDER = null;
function jl_String_$callClinit() {
    jl_String_$callClinit = $rt_eraseClinit(jl_String);
    jl_String__clinit_();
}
function jl_String__init_(var_0) {
    var var_1 = new jl_String();
    jl_String__init_1(var_1, var_0);
    return var_1;
}
function jl_String__init_0(var_0, var_1, var_2) {
    var var_3 = new jl_String();
    jl_String__init_2(var_3, var_0, var_1, var_2);
    return var_3;
}
function jl_String__init_1($this, $characters) {
    var var$2, var$3, $i;
    jl_String_$callClinit();
    var$2 = $characters.data;
    jl_Object__init_0($this);
    var$3 = var$2.length;
    $this.$characters = $rt_createCharArray(var$3);
    $i = 0;
    while ($i < var$3) {
        $this.$characters.data[$i] = var$2[$i];
        $i = $i + 1 | 0;
    }
}
function jl_String__init_2($this, $value, $offset, $count) {
    var $i, var$5;
    jl_String_$callClinit();
    jl_Object__init_0($this);
    $this.$characters = $rt_createCharArray($count);
    $i = 0;
    while ($i < $count) {
        var$5 = $value.data;
        $this.$characters.data[$i] = var$5[$i + $offset | 0];
        $i = $i + 1 | 0;
    }
}
function jl_String_charAt($this, $index) {
    if ($index >= 0 && $index < $this.$characters.data.length)
        return $this.$characters.data[$index];
    $rt_throw(jl_StringIndexOutOfBoundsException__init_());
}
function jl_String_length($this) {
    return $this.$characters.data.length;
}
function jl_String_isEmpty($this) {
    return $this.$characters.data.length ? 0 : 1;
}
function jl_String_equals($this, $other) {
    var $str, $i;
    if ($this === $other)
        return 1;
    if (!($other instanceof jl_String))
        return 0;
    $str = $other;
    if ($str.$length0() != $this.$length0())
        return 0;
    $i = 0;
    while ($i < $str.$length0()) {
        if ($this.$charAt($i) != $str.$charAt($i))
            return 0;
        $i = $i + 1 | 0;
    }
    return 1;
}
function jl_String_hashCode($this) {
    var var$1, var$2, var$3, $c;
    a: {
        if (!$this.$hashCode) {
            var$1 = $this.$characters.data;
            var$2 = var$1.length;
            var$3 = 0;
            while (true) {
                if (var$3 >= var$2)
                    break a;
                $c = var$1[var$3];
                $this.$hashCode = (31 * $this.$hashCode | 0) + $c | 0;
                var$3 = var$3 + 1 | 0;
            }
        }
    }
    return $this.$hashCode;
}
function jl_String__clinit_() {
    jl_String_CASE_INSENSITIVE_ORDER = jl_String$_clinit_$lambda$_81_0__init_();
}
function jl_NoClassDefFoundError() {
    jl_LinkageError.call(this);
}
function cbgm_Matrix4() {
    jl_Object.call(this);
    this.$val = null;
}
var cbgm_Matrix4_tmp = null;
var cbgm_Matrix4_quat = null;
var cbgm_Matrix4_quat2 = null;
var cbgm_Matrix4_l_vez = null;
var cbgm_Matrix4_l_vex = null;
var cbgm_Matrix4_l_vey = null;
var cbgm_Matrix4_tmpVec = null;
var cbgm_Matrix4_tmpMat = null;
var cbgm_Matrix4_right = null;
var cbgm_Matrix4_tmpForward = null;
var cbgm_Matrix4_tmpUp = null;
function cbgm_Matrix4_$callClinit() {
    cbgm_Matrix4_$callClinit = $rt_eraseClinit(cbgm_Matrix4);
    cbgm_Matrix4__clinit_();
}
function cbgm_Matrix4__init_() {
    var var_0 = new cbgm_Matrix4();
    cbgm_Matrix4__init_0(var_0);
    return var_0;
}
function cbgm_Matrix4__clinit_() {
    cbgm_Matrix4_tmp = $rt_createFloatArray(16);
    cbgm_Matrix4_quat = cbgm_Quaternion__init_1();
    cbgm_Matrix4_quat2 = cbgm_Quaternion__init_1();
    cbgm_Matrix4_l_vez = cbgm_Vector3__init_();
    cbgm_Matrix4_l_vex = cbgm_Vector3__init_();
    cbgm_Matrix4_l_vey = cbgm_Vector3__init_();
    cbgm_Matrix4_tmpVec = cbgm_Vector3__init_();
    cbgm_Matrix4_tmpMat = cbgm_Matrix4__init_();
    cbgm_Matrix4_right = cbgm_Vector3__init_();
    cbgm_Matrix4_tmpForward = cbgm_Vector3__init_();
    cbgm_Matrix4_tmpUp = cbgm_Vector3__init_();
}
function cbgm_Matrix4__init_0($this) {
    cbgm_Matrix4_$callClinit();
    jl_Object__init_0($this);
    $this.$val = $rt_createFloatArray(16);
    $this.$val.data[0] = 1.0;
    $this.$val.data[5] = 1.0;
    $this.$val.data[10] = 1.0;
    $this.$val.data[15] = 1.0;
}
function otjw_WebGLContextAttributes() {
    jl_Object.call(this);
}
function otjw_WebGLContextAttributes_create$js_body$_13() {
    return {  };
}
function jl_NoSuchMethodError() {
    jl_IncompatibleClassChangeError.call(this);
}
function jl_NoSuchMethodError__init_(var_0) {
    var var_1 = new jl_NoSuchMethodError();
    jl_NoSuchMethodError__init_0(var_1, var_0);
    return var_1;
}
function jl_NoSuchMethodError__init_0($this, $message) {
    jl_IncompatibleClassChangeError__init_0($this, $message);
}
function cgxgbw_WebGraphics() {
    var a = this; jl_Object.call(a);
    a.$context0 = null;
    a.$canvas2 = null;
    a.$config0 = null;
    a.$gl20 = null;
}
function cgxgbw_WebGraphics__init_(var_0) {
    var var_1 = new cgxgbw_WebGraphics();
    cgxgbw_WebGraphics__init_0(var_1, var_0);
    return var_1;
}
function cgxgbw_WebGraphics__init_0($this, $config) {
    jl_Object__init_0($this);
    $this.$config0 = $config;
    $this.$canvas2 = $config.$canvas0;
    $this.$context0 = $this.$canvas2.$getGLContext0($config);
    $this.$gl20 = cgxgbw_WebGL20__init_($this.$context0);
    $this.$gl20.$glViewport(0, 0, $this.$canvas2.$getWidth(), $this.$canvas2.$getHeight());
    $this.$gl20.$glClearColor(1.0, 1.0, 0.0, 1.0);
    $this.$gl20.$glClear(16384);
}
function cgxgbw_WebGraphics_getGL20($this) {
    return $this.$gl20;
}
function cgxgbtd_TeaWindow() {
    jl_Object.call(this);
    this.$window = null;
}
function cgxgbtd_TeaWindow__init_(var_0) {
    var var_1 = new cgxgbtd_TeaWindow();
    cgxgbtd_TeaWindow__init_0(var_1, var_0);
    return var_1;
}
function cgxgbtd_TeaWindow_getCurrent() {
    return cgxgbtd_TeaWindow__init_(window);
}
function cgxgbtd_TeaWindow__init_0($this, $window) {
    jl_Object__init_0($this);
    $this.$window = $window;
}
function cgxgbtd_TeaWindow_getDocument($this) {
    var $document;
    $document = $this.$window.document;
    return cgxgbtd_TeaDocument__init_($document);
}
function jl_Class() {
    var a = this; jl_Object.call(a);
    a.$name = null;
    a.$platformClass = null;
}
function jl_Class__init_(var_0) {
    var var_1 = new jl_Class();
    jl_Class__init_0(var_1, var_0);
    return var_1;
}
function jl_Class__init_0($this, $platformClass) {
    var var$2;
    jl_Object__init_0($this);
    $this.$platformClass = $platformClass;
    var$2 = $this;
    $platformClass.classObject = var$2;
}
function jl_Class_getClass($cls) {
    var $result;
    if ($cls === null)
        return null;
    $result = $cls.classObject;
    if ($result === null)
        $result = jl_Class__init_($cls);
    return $result;
}
function jl_Class_getName($this) {
    if ($this.$name === null)
        $this.$name = otp_Platform_getName($this.$platformClass);
    return $this.$name;
}
$rt_packages([-1, "java", 0, "lang"
]);
$rt_metadata([jl_Object, "Object", 1, 0, [], 0, 3, 0, ["$getClass0", function() { return jl_Object_getClass(this); }, "$toString", function() { return jl_Object_toString(this); }, "$identity", function() { return jl_Object_identity(this); }],
jl_Throwable, 0, jl_Object, [], 0, 3, 0, ["$fillInStackTrace", function() { return jl_Throwable_fillInStackTrace(this); }],
jl_Exception, 0, jl_Throwable, [], 0, 3, 0, 0,
jl_RuntimeException, 0, jl_Exception, [], 0, 3, 0, 0,
jl_IndexOutOfBoundsException, 0, jl_RuntimeException, [], 0, 3, 0, 0,
ju_Arrays, 0, jl_Object, [], 0, 3, 0, 0,
cbg_ApplicationListener, 0, jl_Object, [], 3, 3, 0, 0,
cbgtde_GearsDemo, 0, jl_Object, [cbg_ApplicationListener], 0, 3, 0, 0,
cbgg_GL20, 0, jl_Object, [], 3, 3, 0, 0,
ji_Serializable, 0, jl_Object, [], 3, 3, 0, 0,
jl_Number, 0, jl_Object, [ji_Serializable], 1, 3, 0, 0,
jl_Comparable, 0, jl_Object, [], 3, 3, 0, 0,
jl_Integer, 0, jl_Number, [jl_Comparable], 0, 3, jl_Integer_$callClinit, 0,
cgxgbwd_ElementWrapper, 0, jl_Object, [], 3, 3, 0, 0,
cgxgbwd_HTMLCanvasElementWrapper, 0, jl_Object, [cgxgbwd_ElementWrapper], 3, 3, 0, 0,
cgxgbtd_TeaCanvas, 0, jl_Object, [cgxgbwd_HTMLCanvasElementWrapper], 0, 3, 0, ["$getWidth", function() { return cgxgbtd_TeaCanvas_getWidth(this); }, "$getHeight", function() { return cgxgbtd_TeaCanvas_getHeight(this); }, "$getGLContext", function(var_1) { return cgxgbtd_TeaCanvas_getGLContext(this, var_1); }, "$getGLContext0", function(var_1) { return cgxgbtd_TeaCanvas_getGLContext0(this, var_1); }],
cgxgbwg_WebGLRenderingContextWrapper, 0, jl_Object, [], 3, 3, 0, 0,
cgxgbt_TeaGLContext, 0, jl_Object, [cgxgbwg_WebGLRenderingContextWrapper], 0, 3, 0, ["$viewport", function(var_1, var_2, var_3, var_4) { cgxgbt_TeaGLContext_viewport(this, var_1, var_2, var_3, var_4); }, "$clear", function(var_1) { cgxgbt_TeaGLContext_clear(this, var_1); }, "$clearColor", function(var_1, var_2, var_3, var_4) { cgxgbt_TeaGLContext_clearColor(this, var_1, var_2, var_3, var_4); }],
jl_Error, 0, jl_Throwable, [], 0, 3, 0, 0,
jl_LinkageError, 0, jl_Error, [], 0, 3, 0, 0,
jl_IncompatibleClassChangeError, 0, jl_LinkageError, [], 0, 3, 0, 0,
jl_NoSuchFieldError, 0, jl_IncompatibleClassChangeError, [], 0, 3, 0, 0,
cgxgbw_WebApplicationConfiguration, 0, jl_Object, [], 0, 3, 0, 0,
jl_Character, 0, jl_Object, [jl_Comparable], 0, 3, jl_Character_$callClinit, 0,
cbgm_Vector, 0, jl_Object, [], 3, 3, 0, 0,
cbgm_Vector3, 0, jl_Object, [ji_Serializable, cbgm_Vector], 0, 3, cbgm_Vector3_$callClinit, ["$set", function(var_1, var_2, var_3) { return cbgm_Vector3_set(this, var_1, var_2, var_3); }],
otci_IntegerUtil, 0, jl_Object, [], 4, 3, 0, 0,
jl_Math, 0, jl_Object, [], 4, 3, 0, 0,
cgxgbw_WebGL20, 0, jl_Object, [cbgg_GL20], 0, 3, 0, ["$glClear", function(var_1) { cgxgbw_WebGL20_glClear(this, var_1); }, "$glClearColor", function(var_1, var_2, var_3, var_4) { cgxgbw_WebGL20_glClearColor(this, var_1, var_2, var_3, var_4); }, "$glViewport", function(var_1, var_2, var_3, var_4) { cgxgbw_WebGL20_glViewport(this, var_1, var_2, var_3, var_4); }],
otji_JS, 0, jl_Object, [], 4, 0, 0, 0,
cgxgbwd_WindowWrapper, 0, jl_Object, [], 3, 3, 0, 0,
jl_CharSequence, 0, jl_Object, [], 3, 3, 0, 0,
cgxgbt_TeaApplicationConfiguration, 0, cgxgbw_WebApplicationConfiguration, [], 0, 3, 0, 0,
otj_JSObject, 0, jl_Object, [], 3, 3, 0, 0,
otjde_EventTarget, 0, jl_Object, [otj_JSObject], 3, 3, 0, 0,
otjde_LoadEventTarget, 0, jl_Object, [otjde_EventTarget], 3, 3, 0, 0,
cbg_Graphics, 0, jl_Object, [], 3, 3, 0, 0,
jl_StringIndexOutOfBoundsException, 0, jl_IndexOutOfBoundsException, [], 0, 3, 0, 0,
cbgm_Quaternion, 0, jl_Object, [ji_Serializable], 0, 3, cbgm_Quaternion_$callClinit, ["$set0", function(var_1, var_2, var_3, var_4) { return cbgm_Quaternion_set(this, var_1, var_2, var_3, var_4); }, "$idt", function() { return cbgm_Quaternion_idt(this); }],
cgxgbwd_HTMLDocumentWrapper, 0, jl_Object, [], 3, 3, 0, 0,
cgxgbtd_TeaDocument, 0, jl_Object, [cgxgbwd_HTMLDocumentWrapper], 0, 3, 0, ["$getCanvas", function(var_1) { return cgxgbtd_TeaDocument_getCanvas(this, var_1); }],
ju_Comparator, 0, jl_Object, [], 3, 3, 0, 0,
jl_String$_clinit_$lambda$_81_0, 0, jl_Object, [ju_Comparator], 0, 3, 0, 0,
jl_AbstractStringBuilder, 0, jl_Object, [ji_Serializable, jl_CharSequence], 0, 0, 0, ["$append0", function(var_1) { return jl_AbstractStringBuilder_append(this, var_1); }, "$insert", function(var_1, var_2) { return jl_AbstractStringBuilder_insert(this, var_1, var_2); }, "$ensureCapacity", function(var_1) { jl_AbstractStringBuilder_ensureCapacity(this, var_1); }, "$toString", function() { return jl_AbstractStringBuilder_toString(this); }],
jl_Appendable, 0, jl_Object, [], 3, 3, 0, 0,
jl_StringBuilder, 0, jl_AbstractStringBuilder, [jl_Appendable], 0, 3, 0, ["$append", function(var_1) { return jl_StringBuilder_append(this, var_1); }, "$insert0", function(var_1, var_2) { return jl_StringBuilder_insert(this, var_1, var_2); }, "$toString", function() { return jl_StringBuilder_toString(this); }, "$ensureCapacity", function(var_1) { jl_StringBuilder_ensureCapacity(this, var_1); }, "$insert", function(var_1, var_2) { return jl_StringBuilder_insert0(this, var_1, var_2); }],
jlr_AnnotatedElement, 0, jl_Object, [], 3, 3, 0, 0,
cbg_Application, 0, jl_Object, [], 3, 3, 0, 0,
cgxgbw_WebApplication, 0, jl_Object, [cbg_Application], 0, 3, 0, ["$init", function() { cgxgbw_WebApplication_init(this); }],
cgxgbte_Launcher, 0, jl_Object, [], 0, 3, 0, ["$build", function() { cgxgbte_Launcher_build(this); }]]);
$rt_metadata([otjde_FocusEventTarget, 0, jl_Object, [otjde_EventTarget], 3, 3, 0, 0,
otjde_MouseEventTarget, 0, jl_Object, [otjde_EventTarget], 3, 3, 0, 0,
otjde_KeyboardEventTarget, 0, jl_Object, [otjde_EventTarget], 3, 3, 0, 0,
otjb_WindowEventTarget, 0, jl_Object, [otjde_EventTarget, otjde_FocusEventTarget, otjde_MouseEventTarget, otjde_KeyboardEventTarget, otjde_LoadEventTarget], 3, 3, 0, 0,
cbg_Gdx, 0, jl_Object, [], 0, 3, 0, 0,
otjb_StorageProvider, 0, jl_Object, [], 3, 3, 0, 0,
otjc_JSArrayReader, 0, jl_Object, [otj_JSObject], 3, 3, 0, 0,
otjb_Window, 0, jl_Object, [otj_JSObject, otjb_WindowEventTarget, otjb_StorageProvider, otjc_JSArrayReader], 1, 3, 0, ["$addEventListener$exported$0", function(var_1, var_2) { return otjb_Window_addEventListener$exported$0(this, var_1, var_2); }, "$removeEventListener$exported$1", function(var_1, var_2) { return otjb_Window_removeEventListener$exported$1(this, var_1, var_2); }, "$get$exported$2", function(var_1) { return otjb_Window_get$exported$2(this, var_1); }, "$removeEventListener$exported$3", function(var_1,
var_2, var_3) { return otjb_Window_removeEventListener$exported$3(this, var_1, var_2, var_3); }, "$dispatchEvent$exported$4", function(var_1) { return otjb_Window_dispatchEvent$exported$4(this, var_1); }, "$getLength$exported$5", function() { return otjb_Window_getLength$exported$5(this); }, "$addEventListener$exported$6", function(var_1, var_2, var_3) { return otjb_Window_addEventListener$exported$6(this, var_1, var_2, var_3); }],
otp_Platform, 0, jl_Object, [], 4, 3, 0, 0,
jl_String, 0, jl_Object, [ji_Serializable, jl_Comparable, jl_CharSequence], 0, 3, jl_String_$callClinit, ["$charAt", function(var_1) { return jl_String_charAt(this, var_1); }, "$length0", function() { return jl_String_length(this); }, "$isEmpty", function() { return jl_String_isEmpty(this); }, "$equals", function(var_1) { return jl_String_equals(this, var_1); }, "$hashCode0", function() { return jl_String_hashCode(this); }],
jl_NoClassDefFoundError, 0, jl_LinkageError, [], 0, 3, 0, 0,
cbgm_Matrix4, 0, jl_Object, [ji_Serializable], 0, 3, cbgm_Matrix4_$callClinit, 0,
otjw_WebGLContextAttributes, 0, jl_Object, [otj_JSObject], 1, 3, 0, 0,
jl_NoSuchMethodError, 0, jl_IncompatibleClassChangeError, [], 0, 3, 0, 0,
cgxgbw_WebGraphics, 0, jl_Object, [cbg_Graphics], 0, 3, 0, ["$getGL20", function() { return cgxgbw_WebGraphics_getGL20(this); }],
cgxgbtd_TeaWindow, 0, jl_Object, [cgxgbwd_WindowWrapper], 0, 3, 0, ["$getDocument", function() { return cgxgbtd_TeaWindow_getDocument(this); }],
jl_Class, 0, jl_Object, [jlr_AnnotatedElement], 0, 3, 0, ["$getName", function() { return jl_Class_getName(this); }]]);
function $rt_array(cls, data) {
    this.$monitor = null;
    this.$id$ = 0;
    this.type = cls;
    this.data = data;
    this.constructor = $rt_arraycls(cls);
}
$rt_array.prototype = Object.create(($rt_objcls()).prototype);
$rt_array.prototype.toString = function() {
    var str = "[";
    for (var i = 0;i < this.data.length;++i) {
        if (i > 0) {
            str += ", ";
        }
        str += this.data[i].toString();
    }
    str += "]";
    return str;
};
$rt_setCloneMethod($rt_array.prototype, function() {
    var dataCopy;
    if ('slice' in this.data) {
        dataCopy = this.data.slice();
    } else {
        dataCopy = new this.data.constructor(this.data.length);
        for (var i = 0;i < dataCopy.length;++i) {
            dataCopy[i] = this.data[i];
        }
    }
    return new $rt_array(this.type, dataCopy);
});
$rt_stringPool(["@", "0", "null", "canvas"]);
jl_String.prototype.toString = function() {
    return $rt_ustr(this);
};
jl_String.prototype.valueOf = jl_String.prototype.toString;
jl_Object.prototype.toString = function() {
    return $rt_ustr(jl_Object_toString(this));
};
jl_Object.prototype.__teavm_class__ = function() {
    return $dbg_class(this);
};
function $rt_startThread(runner, callback) {
    var result;
    try {
        result = runner();
    } catch (e){
        result = e;
    }
    if (typeof callback !== 'undefined') {
        callback(result);
    } else if (result instanceof Error) {
        throw result;
    }
}
function $rt_suspending() {
    return false;
}
function $rt_resuming() {
    return false;
}
function $rt_nativeThread() {
    return null;
}
function $rt_invalidPointer() {
}
main = $rt_mainStarter(cgxgbte_Launcher_main);
(function() {
    var c;
    c = otjb_Window.prototype;
    c.dispatchEvent = c.$dispatchEvent$exported$4;
    c.addEventListener = c.$addEventListener$exported$0;
    c.removeEventListener = c.$removeEventListener$exported$1;
    c.getLength = c.$getLength$exported$5;
    c.get = c.$get$exported$2;
    c.addEventListener = c.$addEventListener$exported$6;
    c.removeEventListener = c.$removeEventListener$exported$3;
})();
})();
