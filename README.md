Rules:

1.	Don't push without an approved pull request
2.	By Tadayon's request, we need one branch per official version, which I 
		translate as each time we change our model by adding layers or 
		changing the number of nodes. 
3.	Don't change the version branch directly. There is a master branch, but
		we won't be using it. We will decide to open a new version, which
		will directly branch off the last version branch. Each team
		member should have their own branch that feeds into each version
		branch. Imagine the flow as below:

->--master-----
  \___V1______________________
	\\\__Katrina branch_///   \__V2_________________
	 \\__Jo's branch____//     
	  \__Cashe's branch_/       

... and so on.
