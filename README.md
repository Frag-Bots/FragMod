# ExampleMod
Template mod with Mixins and Essential.

### Program arguments:
```
--tweakClass
gg.essential.loader.stage0.EssentialSetupTweaker
--mixin
mixins.examplemod.json
```

### VM options:
```
-Dmixin.debug.export=true
-Dmixin.debug.verbose=true
-javaagent:<path-to-mixin.jar>
```

### How to get Mixin JAR path:
- Expand External Libraries
  
![External Libraries](https://i.debuggings.dev/0H38gESl.png)

- Find `Gradle: org.spongepowered:mixin:<version>` and expand it

![Mixin](https://i.debuggings.dev/nqEcOgwB.png)

- Right click on `mixin-<version>.jar`, Click `Copy Path...`, Click `Absolute Path`

![Copy Path](https://i.debuggings.dev/KXkK19u6.png)

![Absolute Path](https://i.debuggings.dev/SZjoBtmj.png)

- And finally, paste your clipboard contents to VM options replacing `<path-to-mixin.jar>`
