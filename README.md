# BearMaps
CS61B Bear Maps

BearMaps is a Google Maps inspired clone for the vicinity of the UC Berkeley campus. It is capable of performing most features you would expect of a mapping application. The "smart" features of the application include map dragging/zooming, map rasterization, A* search algorithm between two points, and an auto-complete search feature.

<img src="images/demo.gif" alt="demo_gif" width="100%"/>

Feature | Description
------- | -------
[RasterAPIHandler](https://github.com/kcheeeung/BearMaps/blob/master/bearmaps/proj2c/server/handler/impl/RasterAPIHandler.java) | Renders map images given a user's requested area and level of zoom.
[AugmentedStreetMapGraph](https://github.com/kcheeeung/BearMaps/blob/master/bearmaps/proj2c/AugmentedStreetMapGraph.java) | Graph representation of the contents of Berkeley Open Street Map data.
[AStarSolver](https://github.com/kcheeeung/BearMaps/blob/master/bearmaps/hw4/AStarSolver.java) | The A* search algorithm to find the shortest path between two points in Berkeley.
[MyTrieSet](https://github.com/kcheeeung/BearMaps/blob/master/bearmaps/proj2c/MyTrieSet.java) | A TrieSet backs the autocomplete search feature, matching a prefix to valid location names in Θ(k) time, where k in the number of words sharing the prefix.
[KDTree](https://github.com/kcheeeung/BearMaps/blob/master/bearmaps/proj2ab/KDTree.java) | A K-Dimensional Tree backs the A* search algorithm, allowing efficient nearest neighbor lookup averaging O(log(n)) time.
[ArrayHeapMinPQ](https://github.com/kcheeeung/BearMaps/blob/master/bearmaps/proj2ab/ArrayHeapMinPQ.java) | A min-heap priority queue backs the A* search algorithm.
