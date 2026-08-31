# MyCampus - Backend Spring Boot

## Avant de démarrer

1. **Configurez votre base de données** : ouvrez `src/main/resources/application.properties`
   et remplacez :
   - `nom_de_votre_base` par le nom réel de votre base PostgreSQL
   - `postgres` par votre nom d'utilisateur PostgreSQL
   - `votre_mot_de_passe` par votre mot de passe

2. **Changez la clé JWT** (ligne `jwt.secret`) avant de rendre le projet,
   par n'importe quelle chaîne aléatoire d'au moins 32 caractères.

3. **Prérequis installés** :
   - Java 17 ou plus récent
   - Maven (ou utilisez le wrapper `./mvnw` si vous en générez un depuis start.spring.io)
   - PostgreSQL avec vos tables déjà créées (script SQL qu'on a fait ensemble)

## Démarrer le serveur

Dans le dossier `backend/` :

```powershell
mvn spring-boot:run
```

Si tout fonctionne, vous verrez dans les logs :
```
Tomcat started on port(s): 8080
```

## Tester rapidement (sans l'app Android)

Avec Postman, ou simplement `curl` :

```powershell
# Inscription
curl -X POST http://localhost:8080/api/auth/register `
  -H "Content-Type: application/json" `
  -d '{\"nom\":\"Diop\",\"prenom\":\"Awa\",\"email\":\"awa@centre.com\",\"motDePasse\":\"motdepasse123\",\"matricule\":\"ET2026-001\",\"filiere\":\"Informatique\",\"niveau\":\"L3\"}'

# Connexion (renvoie un token JWT)
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{\"email\":\"awa@centre.com\",\"motDePasse\":\"motdepasse123\"}'

# Route protégée (remplacez VOTRE_TOKEN par le token reçu au login)
curl http://localhost:8080/api/annonces `
  -H "Authorization: Bearer VOTRE_TOKEN"
```

## Structure du projet

- `entity/` — vos 9 tables représentées en classes Java
- `repository/` — accès à PostgreSQL (générés automatiquement par Spring Data JPA)
- `service/` — logique métier (calcul de moyenne, authentification...)
- `controller/` — routes de l'API REST
- `dto/` — objets d'échange sécurisés (jamais de mot de passe exposé)
- `security/` — gestion des tokens JWT
- `exception/` — codes d'erreur HTTP propres (404, 400, 401...)

## Routes principales

| Méthode | Route | Accès | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Inscription étudiant |
| POST | `/api/auth/login` | Public | Connexion (renvoie un JWT) |
| GET | `/api/etudiants/{id}` | Connecté | Profil d'un étudiant |
| GET | `/api/etudiants/{id}/notes` | Connecté | Notes d'un étudiant |
| GET | `/api/etudiants/{id}/moyenne` | Connecté | Moyenne + statut admis/ajourné |
| GET | `/api/etudiants/{id}/absences` | Connecté | Absences d'un étudiant |
| GET | `/api/cours` | Connecté | Liste des cours |
| GET | `/api/cours/recherche?motCle=...` | Connecté | Recherche de cours |
| GET | `/api/annonces` | Connecté | Liste des annonces |
| GET | `/api/etudiants/{id}/notifications` | Connecté | Notifications d'un étudiant |
| POST/PUT/DELETE | `/api/admin/**` | Admin uniquement | Gestion complète (CRUD) |

## Prochaine étape

Une fois le serveur démarré et testé avec Postman, on connecte l'app Android
en Kotlin via Retrofit sur ces mêmes routes.
