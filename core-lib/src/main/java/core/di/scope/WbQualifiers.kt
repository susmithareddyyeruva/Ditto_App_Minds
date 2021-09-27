package core.di.scope

import javax.inject.Qualifier
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

@Qualifier
@Retention(RUNTIME)
annotation class WbTailornovaApiRetrofit


@Qualifier
@Retention(RUNTIME)
annotation class WbTailornovaBaseUrl

@Qualifier
@Retention(RUNTIME)
annotation class WbMyLibraryApiRetrofit

@Qualifier
@Retention(RUNTIME)
annotation class WbMyLibraryBaseUrl
