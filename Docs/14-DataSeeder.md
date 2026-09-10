# DataSeeder - admin seeding on startup

## Why

The single-admin variant of the portfolio has no `/register` endpoint anymore — it's me who owns it, not an open system with multiple users. So the admin user has to exist in the database *without* anyone registering manually through the API.
The fix: seed the admin automatically every time the app starts, using credentials that live in `.env`, not hardcoded in the source code.

## How

`DataSeeder` implements `CommandLineRunner`. Spring Boot automatically calls `run()` on every bean that implements this interface, right after the whole application context is fully set up, but before the app starts accepting requests.

```java
@Component
public class DataSeeder implements CommandLineRunner {
    @Override
    public void run(String... args) {
        // check if admin already exists, seed if not
    }
}
```

The `.env` values are loaded via `spring.config.import: optional:file:.env[.properties]` in `application.yaml` - native Spring Boot support, no extra dependencies needed.