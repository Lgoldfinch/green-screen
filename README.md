# Green Screen

These docs are written by ChatGPT. So whilst it reads nicely, the true purpose of this project is to be determined! A lot of it is experimental, toying around with Scala 3 and its new features.

## Overview

Green Screen is a modular Scala application designed to bridge OpenBanking data with AI-driven insights. The project is split into two main domains:

1. **Banking**: Integrates with OpenBanking APIs to collect and process financial data for insights.
2. **AI**: Interacts with AI agents and models, leveraging collected data for advanced analytics and automation.

## Project Structure

- **ai/**: Handles AI agent integrations (e.g., Perplexity integration).
- **banking/**: Manages OpenBanking data, with the aim to analyse user transactions and related business logic.
- **common/**: Shared utilities, effects, and configuration used across modules.
- **root/**: Contains the main application entry point and server setup. Aggregates routes from both domains.

## Prerequisites

- [Docker](https://www.docker.com/)
- [SBT](https://www.scala-sbt.org/) (Scala Build Tool)
- Java 21+

## Setup & Running

1. Start required services:
   ```shell
   docker-compose up -d
   ```
2. Run the application:
   ```shell
   sbt run
   ```

## Running Tests

```shell
sbt test
```