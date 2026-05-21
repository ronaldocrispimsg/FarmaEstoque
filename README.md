# FarmaEstoque

FarmaEstoque e um sistema desktop em Java para gestao de estoque farmaceutico. A aplicacao permite administrar medicamentos, fornecedores, usuarios, pedidos de compra, vendas e relatorios a partir de uma interface Swing integrada ao MySQL com Hibernate.

## Tecnologias

- Java 17
- Swing
- Maven
- MySQL
- JPA/Hibernate
- OpenPDF/iText
- jBCrypt

## Requisitos

- JDK 17 ou superior
- Maven instalado
- MySQL Server em execucao
- IDE Java ou terminal com acesso ao Maven

## Configuracao do banco

Crie o banco de dados MySQL usado pela aplicacao:

```sql
CREATE DATABASE farmaestoque;
```

Confira usuario, senha e URL em:

```text
src/main/resources/hibernate.cfg.xml
```

O arquivo `src/main/java/br/com/farmaestoque/ScriptBancoDeDados` contem uma referencia de criacao das tabelas e pode ser usado para revisar a estrutura esperada.

## Como executar

Compile o projeto:

```bash
mvn clean package
```

Execute a classe principal:

```bash
mvn exec:java
```

Classe principal:

```text
br.com.farmaestoque.FarmaEstoque
```

## Funcionalidades

- Login e cadastro de usuarios.
- Cadastro, busca, listagem e exclusao de medicamentos.
- Cadastro, busca, listagem e exclusao de fornecedores.
- Registro e listagem de pedidos de compra.
- Importacao de medicamentos por arquivo CSV.
- Venda de produtos com baixa automatica de estoque.
- Relatorios de vendas e pedidos de compra.
- Geracao de PDF para relatorios.

## Estrutura de pastas

```text
src/main/java/br/com/farmaestoque/   Classes, entidades, servicos e telas Swing
src/main/resources/                  Configuracao Hibernate
remedios para importar/              CSV de exemplo para importacao
```

## Observacoes finais

O FarmaEstoque evoluiu a partir de um CRUD em Java, MySQL e JPA criado no NetBeans, inicialmente estruturado para cadastro de clientes. A versao atual reorganiza o projeto como uma aplicacao de estoque farmaceutico com Maven, autenticacao, controle operacional e relatorios.

Arquivos gerados de build, classes compiladas, logs temporarios e configuracoes locais de IDE nao fazem parte do versionamento recomendado para envio ao GitHub.
