# FarmaEstoque

FarmaEstoque e um sistema desktop em Java para gestao de estoque farmaceutico. A aplicacao permite administrar medicamentos, fornecedores, usuarios, pedidos de compra, vendas e relatorios a partir de uma interface Swing integrada ao MySQL com Hibernate.

## Tecnologias

- Java 17
- Swing
- Maven
- MySQL
- JPA/Hibernate
- OpenPDF/iText

## Como executar
Execute a classe principal:
Atraves do NetBeans

Classe principal:

```text
br.com.farmaestoque.FarmaEstoque
```

## Funcionalidades

- Login e cadastro de usuarios.
- Controle de perfil de usuario com permissao administrativa.
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
