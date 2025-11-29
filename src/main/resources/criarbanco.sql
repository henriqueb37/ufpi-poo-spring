drop database if exists barspring;
create database if not exists barspring;

use barspring;

create table usuarios (
    id integer auto_increment primary key,
    nome_completo varchar(500) not null ,
    email varchar(100) not null unique ,
    senha varchar(200) not null ,
    cargo ENUM('GARCOM', 'ADMIN') not null
);

create table mesas (
    id integer primary key ,
    ativado boolean default true ,
    estado integer not null ,
    paga_entrada boolean default false ,
    n_pessoas integer default 1 ,
    hora_aberta timestamp default null
);

create table tipos_cardapio (
    id integer primary key ,
    ativado boolean default true ,
    nome varchar(100) not null ,
    perc_gorjeta double not null
);

create table cardapio (
    id integer auto_increment primary key ,
    ativado boolean default true ,
    nome varchar(150) not null ,
    valor double not null ,
    tipo integer not null ,
    foreign key (tipo) references tipos_cardapio(id)
);

create table pedidos (
    id integer auto_increment primary key ,
    id_item integer not null ,
    id_mesa integer not null ,
    quant integer not null ,
    valor_fechado double default null ,
    cancelamento varchar(500) default null ,
    hora timestamp default CURRENT_TIMESTAMP ,
    foreign key (id_item) references cardapio(id),
    foreign key (id_mesa) references mesas(id)
);

create table pagamentos (
    id integer auto_increment primary key ,
    id_mesa integer not null ,
    valor double not null ,
    hora timestamp default CURRENT_TIMESTAMP ,
    foreign key (id_mesa) references mesas(id)
);

insert into mesas (id, estado) VALUES (1, 1);
insert into tipos_cardapio (id, nome, perc_gorjeta) VALUES (2, 'Bebidas', 0.10);
insert into tipos_cardapio (id, nome, perc_gorjeta) VALUES (3, 'Comidas', 0.15);
insert into cardapio (id, nome, valor, tipo) VALUES (1, 'Sanduíche de Presunto', 50.0, 3);
insert into cardapio (id, nome, valor, tipo) VALUES (2, 'Cerveja Skil', 501000.0, 2);

insert into pedidos (id_item, id_mesa, quant) VALUES (1, 1, 1);
insert into pedidos (id_item, id_mesa, quant) VALUES (2, 1, 5);

update mesas SET estado = 1 where id = 1;

select * from pedidos;
select * from mesas;

update pedidos set hora = current_timestamp where id = 1 or id = 2;

CREATE TABLE configuracoes (
    id INT PRIMARY KEY,
    valor_couvert DECIMAL(10, 2) NOT NULL DEFAULT 0.00
);

insert into configuracoes (id, valor_couvert) VALUES (1, 5);
