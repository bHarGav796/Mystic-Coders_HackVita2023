# C++ practice questions and answers / Android Dev
1. Hashmaps
Q. given str = "abcbdeaaf" max occuring character?
sol: a=3
     b=2
     c=1
     d=1
     e=1
     f=1    max char a=3
    
   algo_1: a->count ans->a,3
   algo_2: array 26 size
           loop , [ch-'a']++, loop
   
how for str="mera nam ha shukanay ha"? max occuring word?
sol: using hash table😁
     inbuild stuff:
     -> map=O(logn)
     ->unordered-map->O(1)
