package de.thebotdev.rulesbot.util.commandlib;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ArgumentUtil {

    public static Object[] getArguments(Context context, String[] args, int argCount, boolean isVarArgs, Method method) throws NoConverterFoundException, ConversionException, NotEnoughArgumentsException {
        Object[] finalArgs = new Object[argCount + 1 + (isVarArgs ? 1 : 0)];
        finalArgs[0] = context;
        Object[] normalArgs = getConvertedNormalArgs(args, argCount, method, context);
        System.arraycopy(normalArgs, 0, finalArgs, 1, normalArgs.length);
        if (isVarArgs) {
            Parameter p = null;
            for (Parameter pa : method.getParameters()) {
                if (pa.isVarArgs()) {
                    p = pa;
                    break;
                }
            }
            Class<?> clazz = p.getType().getComponentType();
            Object[] varArgs = (Object[]) Array.newInstance(clazz, args.length - argCount);

            for (int i = argCount, j = 0; j < args.length - argCount; i++, j++) {
                varArgs[j] = Converters.convert(clazz, context, args[i]);
            }
            finalArgs[argCount + 1] = varArgs;
        }
        return finalArgs;
    }

    public static Object[] getConvertedNormalArgs(String[] args, int argCount, Method method, Context context) throws NoConverterFoundException, ConversionException, NotEnoughArgumentsException {
        Object[] converted = new Object[argCount];
        Parameter[] parameters = method.getParameters();
        for (int i = 1; i < parameters.length; i++) {
            Parameter p = parameters[i];
            if (p.isVarArgs()) continue;
            try {
                converted[i - 1] = Converters.convert(p.getType(), context, args[i - 1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NotEnoughArgumentsException(e);
            }
        }
        return converted;
    }
}
