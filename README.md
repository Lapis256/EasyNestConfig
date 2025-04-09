# EasyNestConfig

## Usage

```kotlin
object Config : AbstractNestConfig(ModConfig.Type.SERVER, "config") {
    val test = builder.comment("This is a test config")
        .define("test", "This is a test")

    @NestConfig
    @Comment("This is a comment")
    @Translation("test.translation")
    object Test2 {
        val test2 = builder.comment("This is a test config")
            .define("test2", "This is a test")
    }
}

fun main() {
    val helper = ConfigHelper(modContainer, "<Nullable FolderName>")
    helper.registerConfig(Config)
}
```

### Result
```toml
#This is a test config
test = "This is a test"

#This is a comment
[Test2]
	#This is a test config
	test2 = "This is a test"
```

## Add Annotations

```kotlin
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Test

object TestHandler : IApplyHandler {
    override fun apply(clazz: KClass<*>, builder: ModConfigSpec.Builder) {
        clazz.findAnnotation<Test>()?.let { builder.comment("Hello world!!") }
    }
}

object Config : AbstractNestConfig(ModConfig.Type.SERVER, "config") {
    @NestConfig
    @Test
    object Test2 {
        val test2 = builder.comment("This is a test config")
            .define("test2", "This is a test")
    }
}

fun main() {
    val helper = ConfigHelper(modContainer, "<Nullable FolderName>")

    helper.registerApplyHandler(TestHandler)

    helper.registerConfig(Config)
}
```

### Result
```toml
#Hello world!!
[Test2]
	#This is a test config
	test2 = "This is a test"
```
