# MyCampus — App Android (Jetpack Compose)

Frontend Kotlin natif pour ton backend Spring Boot `MyCampus`.

## 1. Intégration dans Android Studio

Deux options :

**A. Nouveau projet à partir de ce dossier**
Ouvre directement ce dossier `mycampus-android` avec Android Studio (File → Open).
Il contient déjà `settings.gradle.kts`, `build.gradle.kts` et le module `app`.

**B. Fusionner dans un projet existant**
Si tu as déjà un projet Android vide créé par le wizard, copie simplement :
- `app/src/main/java/com/mycampus/android/*` → dans ton `app/src/main/java/...`
- `app/src/main/AndroidManifest.xml` (fusionne avec le tien)
- Ajoute les dépendances de `app/build.gradle.kts` aux tiennes.

## 2. Configurer l'URL du backend

Dans `app/build.gradle.kts` :
```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
```

- **Émulateur Android Studio** : garde `10.0.2.2` (alias de `localhost` de ta machine depuis l'émulateur). Ton backend doit tourner sur `localhost:8080`.
- **Téléphone physique en USB/Wi-Fi** : remplace par l'IP locale de ta machine (ex: `http://192.168.1.42:8080/`), trouvable avec `ipconfig` sous Windows. Ton PC et le téléphone doivent être sur le même réseau, et le firewall Windows doit autoriser le port 8080.

⚠️ N'oublie pas le `/` final dans l'URL (requis par Retrofit).

## 3. Démarrer le backend AVANT l'app Android

```powershell
cd C:\MyCampus\backend
mvn spring-boot:run
```

Vérifie que PostgreSQL tourne bien sur le port `5433` (configuré dans `application.properties`).

## 4. Flux d'authentification

1. `LoginScreen` / `RegisterScreen` appellent `AuthRepository.login()` / `.register()`
2. La réponse (`AuthResponse`) est stockée automatiquement dans `TokenManager` (Jetpack DataStore) : token JWT, `etudiantId`, rôle...
3. `AuthInterceptor` attache `Authorization: Bearer <token>` à **toutes** les requêtes suivantes automatiquement — tu n'as jamais à le faire manuellement dans le code UI.
4. Au démarrage de `MainActivity`, si un token existe déjà en local, l'app saute directement au Dashboard (pas besoin de se reconnecter à chaque lancement).

## 5. Architecture

```
data/
  network/       → Retrofit, OkHttp, ApiService (tous les endpoints), gestion d'erreurs
  dto/           → data class miroir des entités/DTO Java du backend
  repository/    → 1 repo par ressource (Cours, Note, Absence, Annonce, Notification, Seance...)
  TokenManager   → session JWT persistée (DataStore)
  AppContainer   → conteneur d'injection manuel (pas de Hilt, volontairement simple)

ui/
  auth/          → Login + Register (écran + ViewModel)
  dashboard/     → Fiche étudiant + moyenne/statut + accès rapide
  cours/         → Liste + recherche des cours
  annonces/      → Liste des annonces
  absences/      → Liste des absences de l'étudiant connecté
  notifications/ → Liste + marquage "lue"
  navigation/    → NavGraph (Navigation Compose)
  ViewModelFactory → factory unique pour tous les ViewModels
```

## 6. Ce qui n'est PAS encore fait (côté admin)

Le rôle `ADMIN` n'a pas d'écran Compose dédié pour l'instant (créer/modifier/supprimer
cours, notes, absences, annonces, séances, enseignants) — seules les routes GET
côté étudiant sont câblées à l'UI. Les méthodes des repositories pour le CRUD admin
existent déjà (`creer`, `modifier`, `supprimer` dans `CampusRepositories.kt`), il ne
reste qu'à créer les écrans Compose correspondants en suivant le même pattern que
`CoursListScreen`/`CoursViewModel`.

## 7. Points d'attention techniques

- **Dates** : le backend sérialise `LocalDate`/`LocalTime`/`LocalDateTime` en chaînes
  ISO-8601 (ex: `"2026-08-31"`, `"14:30:00"`) grâce à
  `spring.jackson.serialization.write-dates-as-timestamps=false`. Côté Kotlin, ces
  champs sont donc typés `String` par simplicité — formate-les à l'affichage si besoin
  (`LocalDate.parse(...)` avec desugaring, ou une simple regex/split).
- **CORS** : déjà ouvert à `*` côté `SecurityConfig.java`, aucune action requise.
- **Cleartext HTTP** : `usesCleartextTraffic="true"` est activé dans le manifest car
  le backend tourne en `http://` pendant le développement. À restreindre avant toute
  publication.
