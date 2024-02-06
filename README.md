<p align="center">
    <img src="./assets/github/capitalALogo2.png" alt="drawing" width="256"/>
</p>

*<p align="center">No mods, no masters.</p>*

# About

capitalA is an open source Discord bot which allows you to moderate without moderators. All administration actions are done through democratic polls and predetermined actions, for example, any user can vote to mute, kick, or ban, any other user and as long as the predetermined amount of votes is reached within the predetermined time limit.

# Why?

I think its fair to assume that the majority of Discord users dislike moderators, however they have always been seen as a necessity. After I saw a gross abuse of power in a Discord server I frequented resulting in my ban, I had the idea for this bot as a way of preventing admin abuse. 

# Usage

You can either use my own instance of the bot (planA) or host your own instance.

## Self-hosting

Firstly, you'll need to install Java and Maven, if you are using NixOS you can simply use the flake to install the proper dependencies. Next, clone the program with `git clone https://github.com/poach3r/capitalA` then compile it with `mvn clean install`, if all goes well it should produce a jar in the target directory.
Now you can run the program with `java -jar ./target/capitala-VERSION.java`

## Commands

You can view all the available commands with `(PREFIX)help`.

## Settings

If you decide to self-host, you can change many settings via the settings.json, this is a brief overview of the important ones.

### token

This is the bot's token, this is necessary if you want the bot to even start.

### huggingFaceKey

Your access key for huggingface.co, this is optional and will disable `(PREFIX)llm` if not set

### prefix

The prefix used before every command.

### votes

This object contains the time limits and required votes for every polling command.

### filters

This object contains the words that are banned by default as well as individual servers banned words.
