# UUDv7 Utility

Tiny utility for creating and parsing v7 **UUID**'s. Developed for use in Quarkus-based applications, but you are free to use it in any other project.

This package makes it easy and safe to work with UUIDv7 in misc applications. 

Use `UUIDv7()` to create a new one. It guarantees version 7 and includes the creation timestamp.

Especially useful, for example, with PostgreSQL: UUIDv7 provides natural ordering and better index performance.  It helps enforce the correct `UUID` version throughout your code.

See [uuid](./src/main/kotlin/org/bpsbits/bps/uuid/readme.md) package for more details.