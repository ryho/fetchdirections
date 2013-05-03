FetchDirections
===============

This is a Java command line application that takes in a permalink to directions on Google Maps, parses it to get the source and destination, submits a Google Maps API request for walking directions, parses the XML that is returned, and saves a text file in the same folder that contains a list of coordinates and steps.


The text file, named DIR.TXT, will have lots of steps, each formatted like this:

latitude

longitude

L or R or U or D or F


L left
R right
U u turn
D continue
F finished


The latitude and longitude are in the following format:

DDMM.MMMMMM

This is the two digits of the degrees concatenated with the minutes. It doesn't give seconds, it just gives a fractional amount of minutes. Yes, this is a dumb way to do it, but it how my Arduino GPS module outputs data and doing a floating point conversion on Arduino takes a lot of cycles.

The negative sign is dropped. If you are going to use this code near the equator or the prime meridian, this will give you trouble. I, however, didn't plan on walking outside of North America for this project.


This code needs internet access to run, since it is submitting an HTTP request to Google's servers.
