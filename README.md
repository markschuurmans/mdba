# Cocktail Explorer

Stap 1 basis voor de app is opgezet met:

- Jetpack Compose UI (`HomeScreen` + `HomeViewModel`)
- Volley networking (`VolleySingleton` + `CocktailRepository`)
- Cocktail data model (`Cocktail`)
- API-call naar `filter.php?c=Cocktail`
- Basis foutafhandeling (error tekst + retry)

Stap 2 is toegevoegd met:

- Navigation Compose (`home` -> `detail/{cocktailId}`)
- Klikbare cocktail-rijen in de home lijst
- Thumbnail rendering via Volley `ImageRequest` (zonder externe image library)
- Detailflow met `lookup.php?i=ID` via `DetailViewModel`
- Ingredienten + instructies op het detail scherm
- `Deel Recept` knop via implicit intent (`ACTION_SEND`)
- Upload eigen foto op detailpagina met implicit intent + lokale opslag van URI
- Bottom navigation met extra galerijscherm voor alle geuploade foto's

## Belangrijke bestanden

- `app/src/main/java/nl/avans/eindopdracht/MainActivity.kt`
- `app/src/main/java/nl/avans/eindopdracht/model/Cocktail.kt`
- `app/src/main/java/nl/avans/eindopdracht/model/CocktailDetail.kt`
- `app/src/main/java/nl/avans/eindopdracht/network/ApiConfig.kt`
- `app/src/main/java/nl/avans/eindopdracht/network/VolleySingleton.kt`
- `app/src/main/java/nl/avans/eindopdracht/data/CocktailRepository.kt`
- `app/src/main/java/nl/avans/eindopdracht/data/CocktailPhotoStore.kt`
- `app/src/main/java/nl/avans/eindopdracht/ui/home/HomeViewModel.kt`
- `app/src/main/java/nl/avans/eindopdracht/ui/home/HomeScreen.kt`
- `app/src/main/java/nl/avans/eindopdracht/ui/common/VolleyNetworkImage.kt`
- `app/src/main/java/nl/avans/eindopdracht/ui/navigation/AppDestinations.kt`
- `app/src/main/java/nl/avans/eindopdracht/ui/detail/DetailScreen.kt`
- `app/src/main/java/nl/avans/eindopdracht/ui/detail/DetailViewModel.kt`
- `app/src/main/java/nl/avans/eindopdracht/ui/detail/DetailUiState.kt`
- `app/src/main/java/nl/avans/eindopdracht/ui/gallery/GalleryScreen.kt`
- `app/src/main/java/nl/avans/eindopdracht/ui/gallery/GalleryViewModel.kt`

## Opmerking

Lokale build-check kon niet worden uitgevoerd in deze omgeving omdat er geen Java Runtime beschikbaar is.
