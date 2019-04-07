package com.topjohnwu.magisk.util

import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.ScopeInstance


/*
 * Injections pulled from library stripped of ComponentCallback dependency
 * NOT TO BE TOUCHED!
 */

fun getKoin(): Koin = GlobalContext.get().koin

inline fun <reified T : Any> inject(
    name: String = "",
    scope: ScopeInstance? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy { get<T>(name, scope, parameters) }

inline fun <reified T : Any> get(
    name: String = "",
    scope: ScopeInstance? = null,
    noinline parameters: ParametersDefinition? = null
): T = getKoin().get(name, scope, parameters)
