package core.di.scope

import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class WbApiRetrofit

@Qualifier
@Retention(RUNTIME)
annotation class WbBaseUrl


@Qualifier
@Retention(RUNTIME)
annotation class WbTokenApiRetrofit


@Qualifier
@Retention(RUNTIME)
annotation class WbTokenBaseUrl
