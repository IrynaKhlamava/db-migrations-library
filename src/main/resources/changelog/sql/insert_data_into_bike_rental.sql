-- insert_data_into_bike_rental
USE bike_rental;

ALTER DATABASE bike_rental CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

ALTER TABLE clients CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE client_accounts CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE locations CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE rates CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE bikes CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE rentals CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

INSERT INTO bike_rental.clients (name, phone)
VALUES
('Alice Johnson', '+48123457701'),
('Bob Smith', '+48123457702'),
('Charlie Brown', '+48123457703'),
('Jan Kowalski', '+48123456701'),
('Anna Nowak', '+48123456702'),
('Piotr Zielinski', '+48123456703'),
('Katarzyna Mazur', '+48123456704'),
('Tomasz Wójcik', '+48123456705'),
('Agnieszka Lewandowska', '+48123456706'),
('Marek Kowalczyk', '+48123456707'),
('Ewa Kamińska', '+48123456708'),
('Marcin Dąbrowski', '+48123456709'),
('Joanna Wiśniewska', '+48123456710'),
('Paweł Jabłoński', '+48123456711'),
('Magdalena Kaczmarek', '+48123456712'),
('Krzysztof Szymański', '+48123456713'),
('Marta Górska', '+48123456714'),
('Adam Piotrowski', '+48123456715'),
('Dorota Michalska', '+48123456716'),
('Łukasz Zawadzki', '+48123456717'),
('Monika Pawlak', '+48123456718'),
('Andrzej Kozłowski', '+48123456719'),
('Sylwia Król', '+48123456720'),
('Grzegorz Jankowski', '+48123456721'),
('Natalia Krawczyk', '+48123456722'),
('Rafał Wojciechowski', '+48123456723'),
('Karolina Nowicka', '+48123456724'),
('Zbigniew Czarnecki', '+48123456725'),
('Beata Sadowska', '+48123456726'),
('Jakub Sikorski', '+48123456727'),
('Elżbieta Janik', '+48123456728'),
('Michał Baran', '+48123456729'),
('Iwona Dudek', '+48123456730'),
('Sebastian Malinowski', '+48123456731'),
('Renata Walczak', '+48123456732'),
('Daniel Kucharski', '+48123456733'),
('Barbara Kubiak', '+48123456734'),
('Patryk Sobczak', '+48123456735'),
('Zofia Lis', '+48123456736'),
('Oskar Stępień', '+48123456737'),
('Weronika Majewska', '+48123456738'),
('Damian Tomaszewski', '+48123456739'),
('Aleksandra Jaworska', '+48123456740'),
('Dominik Wysocki', '+48123456741'),
('Agata Kaźmierczak', '+48123456742'),
('Filip Adamczyk', '+48123456743'),
('Izabela Duda', '+48123456744'),
('Hubert Krupa', '+48123456745'),
('Ewelina Wilk', '+48123456746'),
('Przemysław Olejniczak', '+48123456747'),
('Klaudia Tomczyk', '+48123456748'),
('Adrian Borkowski', '+48123456749'),
('Maria Sokołowska', '+48123456750'),
('Wojciech Wróbel', '+48123456751'),
('Julia Rogalska', '+48123456752'),
('Bartłomiej Sikora', '+48123456753'),
('Emilia Michalak', '+48123456754'),
('Tadeusz Brzeziński', '+48123456755'),
('Natalia Chmielewska', '+48123456756'),
('Kamil Górecki', '+48123456757'),
('Alicja Tomasik', '+48123456758'),
('Piotr Pawłowski', '+48123456759'),
('Maja Czajkowska', '+48123456760'),
('Jacek Żukowski', '+48123456761'),
('Edyta Oleksy', '+48123456762'),
('Ryszard Mielczarek', '+48123456763'),
('Joanna Wojtas', '+48123456764'),
('Mariusz Romanowski', '+48123456765'),
('Gabriela Wojnar', '+48123456766'),
('Dawid Gajewski', '+48123456767'),
('Kamila Szewczyk', '+48123456768'),
('Antoni Woźniak', '+48123456769'),
('Liliana Błaszczyk', '+48123456770'),
('Krzysztof Staniszewski', '+48123456771'),
('Helena Głowacka', '+48123456772'),
('Maciej Trojanowski', '+48123456773'),
('Aleksandra Orłowska', '+48123456774'),
('Paweł Urbaniak', '+48123456775'),
('Anna Zych', '+48123456776'),
('Wiktor Suchocki', '+48123456777'),
('Justyna Michnik', '+48123456778'),
('Jakub Borowski', '+48123456779'),
('Zuzanna Bogucka', '+48123456780'),
('Karol Domański', '+48123456781'),
('Ewa Augustyniak', '+48123456782'),
('Andrzej Słowiński', '+48123456783'),
('Barbara Tomaszewska', '+48123456784'),
('Tomasz Łapiński', '+48123456785'),
('Iwona Majchrzak', '+48123456786'),
('Janusz Szulc', '+48123456787'),
('Magdalena Żmuda', '+48123456788'),
('Bartosz Skowroński', '+48123456789'),
('Katarzyna Wojciechowska', '+48123456790'),
('Konrad Lisowski', '+48123456791'),
('Olga Sienkiewicz', '+48123456792'),
('Artur Wnuk', '+48123456793'),
('Paulina Góral', '+48123456794'),
('Rafał Przybysz', '+48123456795'),
('Aneta Bednarek', '+48123456796'),
('Michał Wawrzyniak', '+48123456797');

INSERT INTO bike_rental.clients (name, phone)
SELECT CONCAT('Client ', id), CONCAT('+48', 
        FLOOR(100000000 + (RAND() * 900000000)) 
    )
FROM (
  SELECT @rownum := @rownum + 1 AS id
  FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) a,
       (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) b,
       (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) c,
       (SELECT @rownum := 0) d
  LIMIT 97
) subquery;

INSERT INTO bike_rental.client_accounts (client_id, balance)
SELECT id, ROUND(RAND() * 100, 2)
FROM bike_rental.clients;

INSERT INTO bike_rental.locations (name, address)
VALUES
('Warszawa - Centrum', 'ul. Marszałkowska 140, 00-061 Warszawa'),
('Kraków - Stare Miasto', 'ul. Floriańska 22, 31-021 Kraków'),
('Gdańsk - Śródmieście', 'ul. Długa 45, 80-831 Gdańsk'),
('Wrocław - Rynek', 'ul. Rynek 13, 50-101 Wrocław'),
('Poznań - Stary Rynek', 'ul. Wielka 10, 61-772 Poznań'),
('Łódź - Piotrkowska', 'ul. Piotrkowska 87, 90-423 Łódź'),
('Szczecin - Centrum', 'ul. Niepodległości 32, 70-404 Szczecin'),
('Lublin - Śródmieście', 'ul. Krakowskie Przedmieście 52, 20-002 Lublin'),
('Katowice - Centrum', 'ul. Mariacka 8, 40-014 Katowice'),
('Bydgoszcz - Stare Miasto', 'ul. Długa 4, 85-034 Bydgoszcz'),
('Gdynia - Centrum', 'ul. 10 Lutego 15, 81-364 Gdynia'),
('Białystok - Centrum', 'ul. Lipowa 12, 15-424 Białystok'),
('Toruń - Stare Miasto', 'ul. Żeglarska 7, 87-100 Toruń'),
('Rzeszów - Centrum', 'ul. Grunwaldzka 6, 35-068 Rzeszów'),
('Kielce - Centrum', 'ul. Sienkiewicza 23, 25-007 Kielce'),
('Zielona Góra - Centrum', 'ul. Kupiecka 15, 65-058 Zielona Góra'),
('Opole - Centrum', 'ul. Krakowska 30, 45-075 Opole'),
('Olsztyn - Centrum', 'ul. Staromiejska 14, 10-018 Olsztyn'),
('Gliwice - Rynek', 'ul. Zwycięstwa 12, 44-100 Gliwice'),
('Częstochowa - Centrum', 'ul. Aleja Najświętszej Maryi Panny 25, 42-202 Częstochowa'),
('Radom - Centrum', 'ul. Żeromskiego 16, 26-600 Radom'),
('Sosnowiec - Centrum', 'ul. 3 Maja 9, 41-200 Sosnowiec'),
('Tarnów - Centrum', 'ul. Wałowa 21, 33-100 Tarnów'),
('Rybnik - Centrum', 'ul. Rynek 18, 44-200 Rybnik'),
('Elbląg - Centrum', 'ul. Stary Rynek 12, 82-300 Elbląg'),
('Płock - Centrum', 'ul. Tumskiego 14, 09-400 Płock'),
('Gorzów Wielkopolski - Centrum', 'ul. Sikorskiego 25, 66-400 Gorzów Wielkopolski'),
('Legnica - Centrum', 'ul. Najświętszej Marii Panny 10, 59-220 Legnica'),
('Kalisz - Centrum', 'ul. Górnośląska 5, 62-800 Kalisz'),
('Wałbrzych - Centrum', 'ul. Główna 15, 58-300 Wałbrzych'),
('Włocławek - Centrum', 'ul. Królewiecka 23, 87-800 Włocławek'),
('Koszalin - Centrum', 'ul. Zwycięstwa 45, 75-001 Koszalin'),
('Zamość - Stare Miasto', 'ul. Grodzka 5, 22-400 Zamość'),
('Nowy Sącz - Centrum', 'ul. Jagiellońska 10, 33-300 Nowy Sącz'),
('Świdnica - Rynek', 'ul. Rynek 4, 58-100 Świdnica'),
('Piła - Centrum', 'ul. Śródmiejska 18, 64-920 Piła'),
('Przemyśl - Stare Miasto', 'ul. Kazimierza Wielkiego 7, 37-700 Przemyśl'),
('Siedlce - Centrum', 'ul. Warszawska 30, 08-110 Siedlce'),
('Starachowice - Centrum', 'ul. Piłsudskiego 20, 27-200 Starachowice'),
('Ostrołęka - Centrum', 'ul. Gen. Hallera 12, 07-410 Ostrołęka'),
('Tychy - Centrum', 'ul. Bielska 8, 43-100 Tychy'),
('Mysłowice - Centrum', 'ul. Katowicka 15, 41-400 Mysłowice'),
('Świętochłowice - Centrum', 'ul. Polna 6, 41-600 Świętochłowice'),
('Jaworzno - Centrum', 'ul. Grunwaldzka 9, 43-600 Jaworzno'),
('Lubin - Centrum', 'ul. Sikorskiego 12, 59-300 Lubin'),
('Oświęcim - Rynek', 'ul. Rynek Główny 5, 32-600 Oświęcim'),
('Ełk - Centrum', 'ul. Wojska Polskiego 7, 19-300 Ełk'),
('Zakopane - Centrum', 'ul. Krupówki 20, 34-500 Zakopane'),
('Augustów - Centrum', 'ul. Mostowa 8, 16-300 Augustów'),
('Suwałki - Centrum', 'ul. Kościuszki 23, 16-400 Suwałki');

INSERT INTO bike_rental.rates (name, price_per_hour)
VALUES
('Standard', 5.00),
('Premium', 10.00),
('VIP', 15.00);

INSERT INTO bike_rental.bikes (model, status, locations_id, rates_id)
SELECT
  CONCAT('Bike ', id),
  'available',
  FLOOR(1 + RAND() * 50), 
  FLOOR(1 + RAND() * 3)   
FROM (
  SELECT @rownum := @rownum + 1 AS id
  FROM (
    SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
  ) a,
  (
    SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
  ) b,
  (SELECT @rownum := 0) c
  LIMIT 100
) subquery;

INSERT INTO bike_rental.rentals (clients_id, bikes_id, rental_start, rental_end)
SELECT
  FLOOR(1 + RAND() * 160), 
  FLOOR(1 + RAND() * 16), 
  NOW() - INTERVAL FLOOR(RAND() * 30) DAY, 
  NOW() - INTERVAL FLOOR(RAND() * 15) DAY 
FROM (
  SELECT @rownum := @rownum + 1 AS id
  FROM (
    SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
  ) a,
  (
    SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
  ) b,
  (SELECT @rownum := 0) c
  LIMIT 100
) subquery;

