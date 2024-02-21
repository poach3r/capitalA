<p align="center">
    <img src="./assets/github/capitalALogo2.png" alt="drawing" width="256"/>
</p>

*<p align="center">No mods, no masters.</p>*

# About

capitalA is an open source Discord bot which allows you to moderate without moderators. All administration actions are done through democratic polls and predetermined actions, for example, any user can vote to mute, kick, or ban, any other user and as long as the predetermined amount of votes is reached within the predetermined time limit.

**[Changelog](./CHANGELOG.md)**

**[Roadmap](./ROADMAP.md)**

# Why?

I think its fair to assume that the majority of Discord users dislike moderators, however they have always been seen as a necessity. After I saw a gross abuse of power in a Discord server I frequented resulting in my ban, I had the idea for this bot as a way of preventing admin abuse. 

# Usage

You can either use my own instance of the bot (currently not available but it should be soon) or host your own instance.

## Instances

A list of bot instances that I know of.

| Name  | Description                                  | Host    | Link |
|-------|----------------------------------------------|---------|------|
| planA | The ONLY official instance,  currently down. | poacher | N/A  |
| planB | My personal testing instance, private.       | poacher | N/A  |

## Commands

Please note that the default prefix is "$" and is required before all commands.

| Command | Arg 1    | Arg 2       | Arg 3  | Action                                                                                      |
|---------|----------|-------------|--------|---------------------------------------------------------------------------------------------|
| set     | filter   | add         | WORD   | Adds the word specified in argument 3 to your server's filter.                              |
| set     | prefix   | STRING      |        | Changes the prefix to the string specified in argument 2.                                   |
| set     | antiRaid | joinMessage | STRING | Changes the message sent to users when they join to the string in argument 3.               |
| set     | antiRaid | joinTime    | INT    | Changes the amount of time before a user can vote to the days of the integer in argument 3. |
| boot    | kick     | USER        |        | Kicks the user mentioned in argument 2.                                                     |
| boot    | ban      | USER        | DAYS   | Bans the user mentioned in argument 2 for the amount of days specified in argument 3.       |
| boot    | mute     | USER        | HOURS  | Mutes the user mentioned in argument 2 for the amount of hours specified in argument 3.     |

## Self-hosting

Firstly, you'll need to install Java and Maven, if you are using NixOS you can simply use the flake to install the proper dependencies. 

Debian/Ubuntu: `sudo apt install default-jre default-jdk maven`

Fedora: `sudo dnf install java-latest-openjdk.x86_64 java-latest-openjdk-devel.x86_64 maven`

Arch: `sudo pacman -S jdk-openjdk jre-openjdk maven`

Windows (scoop): 

```
scoop bucket add java
scoop bucket add main
scoop install openjdk
scoop install main/maven
```

Next, clone the program

`git clone https://github.com/poach3r/capitalA` 

Then compile it with Maven

`mvn clean install`

If all goes well it should produce a jar in the target directory. Now you can run the program.

`java -jar ./target/capitalA-VERSION.java`

### Settings

The only setting you need to change is the token specified in the object "token". 

## [Plugins](./PLUGINS.md)

