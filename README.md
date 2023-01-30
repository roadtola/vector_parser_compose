# VectorParserCompose

## Description

VectorParserCompose helps to find and manipulate inner elements of vector drawables like path and group.Extension and update of abandoned repo devendroid/VectorChildFinder.

## Preview

![trainning_logger](/Animation.gif)

### Step 1. Add the JitPack repository to your root build.gradle

```gradle
  allprojects {
      repositories {
        ...
        maven { url 'https://jitpack.io' }
      }
    }
```

## Step 2. Add the dependency

```gradle
  dependencies {
            implementation 'com.github.roadtola:vector_parser_compose:1.0.0'
    }
```

## Usage

### Compose

Builded on AsyncImage

```kotlin
VectorParser(
  modifier = Modifier
  vectorResource = R.drawable.ic_front_muscles,
  contentDescription = "muscle man",
  contentScale = ContentScale.FillBounds
) { paths, group ->
    // paths and groups that you have named in your vector file
    // on every render new paths and groups are created
    group.forEach {
        it.value.setAlpha(0.3f)
    }
    group["chest"]?.setAlpha(0.9f)
    paths["eye_left"]?.fillColor = Color.Blue.hashCode()
  }
```

### Or

Builded on AndroidView

```kotlin
VectorParserView(
  modifier = Modifier
  vectorResource = R.drawable.ic_front_muscles,
  contentDescription = "muscle man",
  contentScale = ImageView.ScaleType.FIT_XY
) { paths, group ->
    // paths and groups that you have named in your vector file
    // on every render new paths and groups are created
    group.forEach {
        it.value.setAlpha(0.3f)
    }
    group["chest"]?.setAlpha(0.9f)
    paths["eye_left"]?.fillColor = Color.Blue.hashCode()
  }
```

### Basic

