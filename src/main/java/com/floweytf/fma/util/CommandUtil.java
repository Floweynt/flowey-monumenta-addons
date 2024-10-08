package com.floweytf.fma.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;

public class CommandUtil {
    @SafeVarargs
    public static <A, T> RequiredArgumentBuilder<A, T> arg(String n, ArgumentType<T> a, ArgumentBuilder<A, ?>... t) {
        final var inst = RequiredArgumentBuilder.<A, T>argument(n, a);
        for (var callback : t) {
            if (callback != null) {
                inst.then(callback);
            }
        }
        return inst;
    }

    @SafeVarargs
    public static <A, T> RequiredArgumentBuilder<A, T> arg(String k, ArgumentType<T> a, Command<A> f,
                                                           ArgumentBuilder<A, ?>... t) {
        return arg(k, a, t).executes(f);
    }

    @SafeVarargs
    public static <A, T> RequiredArgumentBuilder<A, T> arg(String k, ArgumentType<T> a, Command<A> f,
                                                           SuggestionProvider<A> suggest, ArgumentBuilder<A, ?>... t) {
        return arg(k, a, t).executes(f).suggests(suggest);
    }

    @SafeVarargs
    public static <A> LiteralArgumentBuilder<A> lit(String n, ArgumentBuilder<A, ?>... t) {
        final var inst = LiteralArgumentBuilder.<A>literal(n);
        for (var callback : t) {
            if (callback != null) {
                inst.then(callback);
            }
        }
        return inst;
    }


    @SafeVarargs
    public static <A> LiteralArgumentBuilder<A> lit(String key, Command<A> execute,
                                                    ArgumentBuilder<A, ?>... callbacks) {
        return lit(key, callbacks).executes(execute);
    }

    @SafeVarargs
    public static <A> LiteralArgumentBuilder<A> lit(String key, Command<A> execute,
                                                    Predicate<A> cond,
                                                    ArgumentBuilder<A, ?>... callbacks) {
        return lit(key, callbacks).executes(execute).requires(cond);
    }

    @SafeVarargs
    public static LiteralArgumentBuilder<CommandSourceStack> mcLit(
        String key, ArgumentBuilder<CommandSourceStack, ?>... callbacks
    ) {
        return lit(key, callbacks);
    }

    @SafeVarargs
    public static LiteralArgumentBuilder<CommandSourceStack> mcLit(
        String key, Command<CommandSourceStack> execute, ArgumentBuilder<CommandSourceStack, ?>... callbacks
    ) {
        return lit(key, execute, callbacks);
    }
}