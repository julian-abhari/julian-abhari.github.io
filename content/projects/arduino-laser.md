---
id: arduino-laser
category: projects
title: "Arduino Laser"
organization: null
startDate: 2018-06
endDate: 2018-07
tags: ["java", "csharp", "arduino", "raspberry-pi", "hardware"]
summary: "A wirelessly controlled cat toy: an Arduino-driven laser steered by my mouse position through a Raspberry Pi server."
featured: false
techStack: ["java", "csharp", "arduino", "raspberry-pi"]
repoUrl: null
demoUrl: null
links: []
media: []
---

In the summer between my Freshman and Sophomore years of high-school, I was faced with the problem of waking up to a playful cat, but not having the motivation to get out of bed. So I did what any normal person would do, I took out my Arduino and raspberry pi, and got to work creating a wirelessly controlled toy to play with my cat. I simply programmed the Arduino to move the given x and y motors by a specified amount when fed data from its USB input.

The trick that really made it magical though, was the fact that the Arduino wasn't receiving info directly from my computer, but rather from the Raspberry Pi which was running a different program that turned it into a server receiving info wirelessly from a program I made running on my computer that transmitted my mouse's x and y location.
