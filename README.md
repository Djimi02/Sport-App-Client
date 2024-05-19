# TeamTally - Track your Sport Game Statistics with Friends

Technologies: Android Studio(with java), Retrofit, Spring Boot

This is the client side of my project. If you want to see the back-end server you can check my [repository](https://github.com/Djimi02/Sport-App-Server). 

First, I will describe the purpose of the app and what it looks like. For technical information please scroll down.

NOTE: The current version of the application is just a demo and the UI is yet to be designed.

## Application purpose
TeamTally is app designed for people, who regularly play team sports with friends and would like to keep track of their game statistics.

The app allows users to create sport specific groups. Inside these groups, users can add members that represent their friends. Each member in a group has sport specific statistics. Members of a group can be associated with an app user. The creator of a group can send a code to their friends, which can be used to join their group. Upon joining, users can choose between joining the group as new (fresh) member or joining as (being associated with) an already existing member in the group. Members in a group can create games. The game creation process includes: choosing teams and setting each member's game statistics. Saving a game updates each member's statistics, for the members who participated in the game. In this way the application keeps track of the statistics per member and per game.

Visual content of the application follows.

NOTE: The current version of the application is just a demo and the UI is yet to be designed.

### Register/Login

<div align="center">
	<img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/register.jpg">
    <img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/login.jpg">
</div>


### Homepage

<div align="center">
	<img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/homepage.jpg" alt = "Homepage Overview">
    <img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/homepage_add_group.jpg" alt = "Creating Group">
</div>

### Football Group

<div align="center">
	<img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/fb_group.jpg" alt = "FB Group Overview">
    <img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/group_settings.jpg" alt = "Group Settings">
</div>

In settings, the admin of the group can change other members' roles or delete the group. A member can be: Member, Game-Maker (allowed to create/delete games) or Admin(allowed to kick members, delete group, change other member's roles).

<div align="center">
	<img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/game_view.jpg" alt = "Viewing a saved game">
    <img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/group_add_member.jpg" alt = "Adding Member">
</div>

### Game Creation

<div align="center">
	<img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/game_step_1.jpg" alt = "Choosing Teams">
    <img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/game_step_2.jpg" alt = "Game Creation Step 2">
</div>

Teams can be selected manually(drag&drop in the desired team column) or randomly(drag&drop the desired players to additional column and then pressing "Generate" button).

<div align="center">
	<img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/game_step_2_stat_selection.jpg" alt = "Selecting Sport Specific Stats for each member">
    <img width = "20%" src="https://github.com/Djimi02/Sport-App-Client/blob/main/images/game_step_3.jpg" alt = "Finalizing Game Creating">
</div>

## Technical desciption
This application is the client side of the project. It communicates with a Spring Boot server containing PostgreSQL database using http requests. For more information, you can check my [repository](https://github.com/Djimi02/Sport-App-Server).

Since the idea of the application is to include multiple sports, I tried to make my code as extendable as I could. For this reason, I used Strategy Design pattern for loading the sport specific group/game fragment in the group/game activity. Furthermore, since much of the functionality between the different sports in the game and group fragmnets overlap, Template Design Pattern is appliad to minimize the code duplication.