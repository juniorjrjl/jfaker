# Introdução

Bem vindo a wiki da JFaker, aqui você irá encontrar tudo que você precisa pra utilizar a JFaker em seus Projetos

## O que é a JFaker

A JFaker é uma lib que faz uso de annotation processor para criar classes de bots que criam instancias das classes de seu projeto com dados aleatórios para você se preocupar somente com os seus cenários de testes, segue abaixo um exemplo de teste sem o JFaker e um exemplo com JFaker:

vamos imaginar que você esteja testando uma classe que faz o mapeamento de um determinado objeto para outro objeto

exemplo de um código sem o uso de JFaker ( com dados fixos)
```
// configuraçoes da sua classe com sua lib de testes
void mapTest(){
    var entity = new UserEntity();
    entity.setId(1);
    entity.setName("name");
    var dto = testClass.toDTO(entity);
    //asserções do seu código
}
```

exemplo de um código usando JFaker ( com dados aleatórios deixando seu teste mais dinâmico)
```
// configuraçoes da sua classe com sua lib de testes
void mapTest(){
    var entity = UserEntityBot
        .builder()
        .build();
    var dto = testClass.toDTO(entity);
    //asserções do seu código
}
```
E a melhor parte é que essa classe UserEntityBot é gerada automaticamente toda vez que você fizer build do seu código de teste fazendo simples configurações

## Começando a usar o JFaker

Siga os seguintes passos:

1 - Para usar o JFaker em seu projeto, basta adicionar as dependências a seguir:

```
```

O JFaker utiliza a lib Datafaker internamente nos bots para a geração dos dados randomicos( para mais informações de como a Datafaker funciona [aqui](https://www.datafaker.net) tem um link para sua documentação).

2 - Extenda a classe `net.datafaker.Faker` da lib Datafaker e vamos começar a configurar a criação de nossos bots:

```
//imports

@FakerInfo(
    botsConfiguration = {
        @AutoFakerBot(
            generatedInstance = "br.com.sample.UserEntity",
            packageToGenerate = "br.com.sample.bot",
            botBuildStrategy = @BotBuildStrategy(
                setterStrategy = @SetterStrategy
            )
        )
    }
)
public class MyCustomFaker extends Faker {

}

```

No codigo acima estamos informando que queremos gerar um Bot para a classe UserEntity no package `br.com.sample.bot` e o bot irá inserir os dados na classe usando os setters da classe.

Agora você está pronto para começar a usar o seu UserEntityBot que será gerado com um código parecido com o código abaixo:

```
// imports omitidos
public class UserEntityBot extends AbstractBot<UserEntity> {
    private MyCustomFaker faker = new MyCustomFaker();

    private Supplier<Long> id = () -> (long) faker.number().positive();
    private Supplier<String> name = () -> faker.lorem().word();

    public BookModelBot withId(final Supplier<Long> id) {
        this.id = id;
        return this;
    }

    public BookModelBot withName(final Supplier<String> name) {
        this.name = name;
        return this;
    }

    public BookModel build() {
        var userEntity = new br.com.sample.UserEntity();
        userEntity.setId(id.get());
        userEntity.setName(name.get());
        return userEntity;
    }
}

```

É possível popular suas instancias usando outras estratérias ( consultar na documentação a parte onde falamos sobre @BotBuildStrategy)

Como você percebeu o nosso bot nos permite definir para qualquer campo um dado fixo para os cenários onde precisamos de alguma informação especifica em uma propriedade da nossa classe, para isso usamos os métodos with.

Todos os bots gerados por padrão irão extender a classe
`AbstractBot` para disponibilizar alguns métodos, o metodo build sem argumentos para a criação da instância com dados aleatórios e uma sobrecarga que recebe um long como argumento e gera uma lista com X bots, onde x é o número passado como argumento para o build ( para mais informações da classe `AbstractBot`)

# debug

```
./gradlew --no-daemon -Dorg.gradle.debug=true clean build
```