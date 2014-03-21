Transit Trails
===============
Visualization of bus location data before and after major transit cuts.

1. Camille Cobb cobbc12@cs.washington.edu
2. Caitlin Bonnar  cbonnar@cs.washington.edu
3. Yi Pan  pany5@uw.edu
4. Katie Kuksenok kuksenok@cs.washington.edu

Abstract: 

We created an interactive visualization intended to depict the effect of route cuts in Puget Sound over a year’s time (specifically, March 2011- March 2012), and allow exploration of congestion data by day of week and time of day.
To support this task, we designed and implemented an interactive visualization depicting historical OneBusAway data, which consist of individual bus positional check-ins every ten seconds and their deviation from schedule at that time and location. Our final visualization includes a side-by-side comparison of points on a map of Puget Sound. Points represent a location where at least one bus checked in at within a time interval specified by the user. The color of each point represents the average deviation of buses that checked in at that location. We conducted eight user studies with transit experts and regular bus riders in order to evaluate and test our design and elicit feedback about how well we supported these goals. We then performed iterative enhancement to improve interactions and layout. Although we believe that the available data were not sufficiently complete to warrant more detailed exploration, the product we created accomplishes the goals of facilitating exploration of congestion and coverage differences resulting from transit changes and serves as a useful starting point for future visualizations once data collection has been improved.  

![Overview](https://github.com/CSE512-14W/fp-cobbc12-cbonnar-pany5-kuksenok/raw/master/FinalVersion.png)

[Poster](https://github.com/CSE512-14W/fp-cobbc12-cbonnar-pany5-kuksenok/raw/master/poster/TransitTrails_poster.pdf),
[Final Paper](https://github.com/CSE512-14W/fp-cobbc12-cbonnar-pany5-kuksenok/raw/master/TransitTrailsFinal.pdf)

## Running Instructions

Access our visualization at http://homes.cs.washington.edu/~cbonnar/viz/busviz.html or download this repository and run `python -m SimpleHTTPServer 9000` and access this from http://localhost:9000/.

