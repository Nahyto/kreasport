Krea Sport
==========

Description du projet:
======================

Krea Sport est un serveur accueillant un base de donnée répondant à des requêtes REST, un site web ainsi qu'une application Android pour des courses d'orientation. Le site web permet à l'administrateur des courses de créer des parcours. Le serveur les stocke et les sert aux clients sur l'application.

Progression
-------
**Côté serveur + site:**

**Côte application:**
L'application permet de télécharger des parcours du serveur à travers un clé spéciale ou tous les parcours ouverts sur le site.
L'affichage de la carte est dynamique, un parcours démarré mais en pause ne présente pas le me icône qu'un parcours non commencé. Aussi, les balises validées validées sont marquées par une croix
La fonctionnalité course d'orientation est complète :
On peut démarrer un parcours avec un chronomètre affichant le temps écoule
Les balises sont validées automatiquement par géolocalisation.

----------


Description du repo
===================

Le repo présente deux dossier à sa racine:

 - **jersey-skeleton-master**: le code du serveur et du site se situent dans le dossier **server**
 - **Mapsv3**: le dossier racine du projet Android Studio.


----------


Description du serveur
======================


----------


Description du site
===================


----------


Description de l'application
============================

L'application présente une Activity principale `MainActivity`ayant un fragment principale. Ce fragment s'alterne pour montrer deux écrans différents selon le choix dans la menu glissant à gauche : `Home` et `Explore`.

Utilisation de l'écran "Home"
-------
Cet écran présente un champ de texte et deux boutons. Le champ de texte permet d'entrer une clé fournie par l'administrateur des jeux. 

Sur validation du premier bouton, l'application demande au serveur le parcours associé à cette clé. Si l'application réussit à télécharger le parcours, il le sauvegarde et puis change d'écran pour afficher la carte avec le nouveau parcours.

Sur validation du second bouton, l'application télécharge tous les parcours disponibles ouvertement et puis affiche l'écran Explore avec ces parcours.

Utilisation de l'écran "Explore"
-------

Cet écran présente une carte avec des markers bleus représentant des débuts de parcours.
Une fois un parcours sélectionne, un menu glissant apparaît en bas de l'écran. Elle donne des informations sur le parcours tels que le titre, sa description, sa difficulté...

Sur la droite de ce menu, une bouton "Play" apparaît si aucun parcours n'a encore été lancé. Une fois ce bouton validé, le menu du bas se transforme pour montrer le temps écoulé depuis le début du parcours et la progression sur le nombre de balises découverts.

La validation de la balise se fait automatiquement lorsque l'utilisateur s'approche suffisamment près.
