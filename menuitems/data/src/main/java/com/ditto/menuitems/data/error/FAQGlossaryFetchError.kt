package com.ditto.menuitems.data.error

import non_core.lib.error.FeatureError


open class FAQGlossaryFetchError(
    message: String = "FAQ and Glossary  fetch Failed",
    throwable: Throwable? = null
) : FeatureError(message, throwable)