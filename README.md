<p align="center">
    <img src="./assets/github/capitalALogo2.png" alt="drawing" width="256"/>
</p>

*<p align="center">No mods, no masters.</p>*

# About

capitalA is an open source Discord bot which allows you to moderate without moderators. All administration actions are done through democratic polls and predetermined actions, for example, any user can vote to mute, kick, or ban, any other user and as long as the predetermined amount of votes is reached within the predetermined time limit.

# Why?

I think its fair to assume that the majority of Discord users dislike moderators, however they have always been seen as a necessity. After I saw a gross abuse of power in a Discord server I frequented resulting in my ban, I had the idea for this bot as a way of preventing admin abuse. 

# Usage

You can either use my own instance of the bot (currently not available but it should be soon) or host your own instance.

## Commands

Please note that the default prefix is "$" and is required before all commands.

| Command | Arg 1  | Arg 2  | Arg 3 | Action                                                                                  |
|---------|--------|--------|-------|-----------------------------------------------------------------------------------------|
| set     | filter | add    | WORD  | Adds the word specified in argument 3 to your server's filter.                          |
| set     | prefix | STRING |       | Changes the prefix to the string specified in argument 2.                               |
| boot    | kick   | USER   |       | Kicks the user mentioned in argument 2.                                                 |
| boot    | ban    | USER   | DAYS  | Bans the user mentioned in argument 2 for the amount of days specified in argument 3.   |
| boot    | mute   | USER   | HOURS | Mutes the user mentioned in argument 2 for the amount of hours specified in argument 3. |

## Self-hosting

Firstly, you'll need to install Java and Maven, if you are using NixOS you can simply use the flake to install the proper dependencies. Next, clone the program with `git clone https://github.com/poach3r/capitalA` then compile it with `mvn clean install`, if all goes well it should produce a jar in the target directory.
Now you can run the program with `java -jar ./target/capitala-VERSION.java`

## Settings

If you decide to self-host, the only setting you need to change is the token specified in the object "token". 
