package com.gem.framework.utils

open class EngineException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class ResourceLoadException(message: String, cause: Throwable? = null) : EngineException(message, cause)

class ShaderCompilationException(message: String) : EngineException(message)

class ComponentException(message: String, cause: Throwable? = null) : EngineException(message, cause)

class CopyException(message: String, cause: Throwable? = null) : EngineException(message, cause)
