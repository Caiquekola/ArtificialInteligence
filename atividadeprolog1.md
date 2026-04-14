
usuario(carlos).
usuario(joana).
usuario(felisberto).
usuario(laura).
usuario(julio).
usuario(igor).
usuario(wilson).
usuario(marta).
usuario(agnes).

produto(iphone).
produto(motox).
produto(livro_harry_potter).
produto(camisa_star_wars).
produto(playstation).
produto(xbox).
produto(windows_phone).
produto(ipad).
produto(imac).
amigo(carlos, joana).
amigo(laura, carlos).
amigo(laura, igor).
amigo(laura, marta).
amigo(julio, igor).
amigo(julio, wilson).
amigo(marta, felisberto).
amigo(felisberto, carlos).
amigo(felisberto, agnes).
ehamigo(X, Y) :- amigo(Y, X).
ehamigo(X,Y) :- amigo(X,Y).

comprou(carlos, motox). 
comprou(felisberto, xbox).
comprou(laura, iphone).
comprou(agnes, playstation).
comprou(igor, ipad).
comprou(marta, ipad).
curtiu(carlos, ipad).
curtiu(agnes, wilson).
curtiu(laura, xbox).
curtiu(laura, playstation).

talvez_voce_conheca(X, Y) :- ehamigo(X,Z),ehamigo(Y,Z), X \= Y, \+ ehamigo(X,Y).

recomendamos(X,Y) :- curtiu(Y,X);comprou(Z,X),ehamigo(Y,Z).
