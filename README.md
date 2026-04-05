# Cocktail Explorer

Eindopdracht voor MDBA

## Functionaliteiten

- **Overzicht & Details:** Een lijst van cocktails in een `LazyColumn` met doorklikmogelijkheid naar een gedetailleerd receptenscherm.
- **API Integratie:** Gebruik van `Volley` voor het ophalen van data (JSON).
- **Interactie & Intents:**
    - Recepten delen via `ACTION_SEND`.
    - Eigen foto's voor cocktails kiezen met de `OpenDocument` intent.
- **Data Opslag:** Lokale opslag van foto-referenties (URI's) voor een gepersonaliseerde ervaring.
- **Navigatie:** Gebruik van `Navigation Compose` voor een soepele ervaring tussen het overzicht, de details en het galerijscherm.
- **Responsive UI:** De layout past zich automatisch aan bij wisseling tussen portrait en landscape oriëntatie.
- **Runtime Permissions:** Vraagt om de nodige permissies voor toegang tot afbeeldingen.

## Architectuur

- **Repository Pattern:** Voor een goede scheiding tussen data-ophalen en de UI.
- **MVVM:** Gebruik van `ViewModel` en `UiState` om data veilig en asynchroon (coroutines) te verwerken.
- **Jetpack Compose:** Volledig opgebouwd in declaratieve UI.
