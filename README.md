![banner](https://raw.githubusercontent.com/AantonM/g.e.m.a./master/readme/ic_gema.png) 


# Gameplayer Emotional Modeling Application - G.E.M.A. 


<h2 id="desciption">Description :scroll:</h2>

An Android application that determines the game player's emotional state during gameplay. This project investigates a method to capture the user's interactions with a game and analyses how the emotional state of the user changes between the different levels of the gameplay difficulty. 

The application applies several methodologies to handle fuzzy data when processing the results. It makes use of external facial recognition API that analyses the emotional state from a portrait photo (Microsoft Azure Face API). 

<h2 id="problem">The problem :confused:</h2>

With the increase of the gaming industry, game developers are trying to find a way to produce more intense products, which led to the development of a Dynamic Game Difficulty Balancing framework. The purpose of this framework is to maintain the game player’s interest by modifying the game settings depending on the user’s emotional feelings.

This project provides a solution that can be used by a closed group of game developers, that would like to investigate how their gameplay affects the customers.

<h2 id="solution">The solution :bulb:</h2>

Once a game is initiated the application takes photos of the gameplayer's face and sends them to the Azure Face API. The API returns the emotional state of the user, which is displayed in real-time (can be switched off). Once the gameplay is completed the application produces a report (visual & exported JSON file) that shows the overall emotional state of the player and the areas of the game where the user wasn't feeling as expected.

The results, provided by this project, can be used by game creators and content creators to tune their gameplay or product to produce more affective experience.

<h2 id="flow">Flow :walking:</h2>

1. The user launches the application and fills the user details & selects the game mode.

2. The user plays the game.

3. Once the gameplay is completed the application produces a report showing the player emotional state during the game.

<h2 id="screenshots">Screenshots :clapper:</h2>

![alt tag](https://raw.githubusercontent.com/AantonM/g.e.m.a./master/readme/gameplay_scrn.png)

<h2 id="help">Open-source projects used :clap:</h2>

 - game          : [Path of Ants](https://github.com/zunair-syed/Path_of_Ants)
 - chat library  : [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)


