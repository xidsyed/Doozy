---
trigger: always_on
---

When using koin modules for dependency injection, feature level modules are included into the `MainModule`, and use the `org.koin.plugin.module.dsl.*` the compiler plugin dls isntead of the `org.koin.core.module.dsl.*`.