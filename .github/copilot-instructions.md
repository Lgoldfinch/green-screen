# Copilot Instructions for This Project

This project uses **Scala 3** with **Cats Effect 3**.  
When suggesting code completions, please follow these guidelines:

## General
- Prefer **functional programming style** with immutability and referential transparency.
- Use **Scala 3 syntax** (e.g., significant indentation, given/using, enums, extension methods).
- Avoid deprecated or Scala 2.x syntax.

## Cats Effect
- Use `IOApp.Simple` for entry points.
- Use `IO`, `Resource`, and `Deferred/Ref` when handling side effects or concurrency.
- Prefer **compositional** code (e.g., `flatMap`, `*>`, `parTraverse`) instead of imperative constructs.
- For console output, prefer `IO.println` instead of `println`.

## Testing
- Use **MUnit** with **cats-effect-testing** for effectful tests.

## Code Style

- Use **given instances** and **type classes** idiomatically.
- Prefer `for`-comprehensions for sequencing `IO` when readability improves.
- Keep functions small and composable.
- Do not throw exceptions. Raise errors into the `IO` / `F` context.

## Code generation

### Data Models

When asked to create a new class for a data model, build its constituents using suitable opaque types, refined types and
enums where applicable.

In the companion object for opaque types, provide an apply method for construction and a value extension method for extraction.

This is an example:
```scala 3
opaque type GreenestScreen = NonEmptyString

object GreenestScreen {
  def apply(nes: NonEmptyString): GreenestScreen = nes

  extension (gs: GreenestScreen) def value: NonEmptyString = gs
}

enum ScreenType:
    case OLED, 
         LCD,
         LED,
         RETINA

final case class Screen(
    screenType: ScreenType,
    greenestScreen: GreenestScreen
)
```

### Context Bounds

When asked to create a new function or class that requires a context bound, use the following syntax:

```scala 3
def myFunction[F[_]: { Decoder, Encoder} ](param: Int): F[Int] = ???
```

Instead of the Scala 2 syntax or 

```scala 3
def myFunction[F[_]](param: Int)(using Decoder[F], Encoder[F]): F[Int] = ???
```