/* Versão atualizada em 2025
 * Atividade Algorimtos Genéticos - Inteligência Artificial
 * Problema da escala de viagens
 */

/**
 *
 * @author Angelo
 * Esta implementação considera apenas vôos de ida
 */

public class ProblemaViagensGrupoAlgoritmosGeneticos {
    static String[] aeroportos = {"ABC","BDE","GHI","FFW","DDI","GWQ","KAI","XYZ","PSS","VBN","JJJ","SAD","QRT","DRR","XZF"};    
    
    Cliente[] clientes;
    Voo[] voos;
    Individuo[] populacao;
    
    public static void main(String[] args){
        ProblemaViagensGrupoAlgoritmosGeneticos pvgag = new ProblemaViagensGrupoAlgoritmosGeneticos();
        pvgag.aplicaAlgoritmo(8);
        
    }
    
    public void aplicaAlgoritmo(int numeroIteracoes){
      voos = Dados.getVoos();
      clientes = Dados.getClientes();
      gerarPopulacao(30);//Gera população inicial aleatoriamente
      printDadosPopulacao(false);//imprime os dados da populacao
      for(int i = 0; i < numeroIteracoes; i++){
        double[] roleta = montarRoleta(populacao);                                           
        Individuo[] populacaoSelecionada = selecaoNatural(15, roleta);//ir ajustando número de selecionados para melhorar resultados
        //gera nova populacao
        Individuo[] populacaoNova = new Individuo[populacao.length];
        for(int c = 0; c < populacaoSelecionada.length; c++){
            populacaoNova[c] = populacaoSelecionada[c];
        }
        //preenche o restante da populacao com cruzamentos
        for(int c = populacaoSelecionada.length; c < populacao.length; c++){
            //seleciona progenitores aleatoriamente a partir dos sobreviventes:
            int sorteio1 = (int)(Math.random()*populacaoSelecionada.length);
            int sorteio2 = 0;
            do{//para evitar que pegue o mesmo indivíduo duas vezes como progenitor
                sorteio2 = (int)(Math.random()*populacaoSelecionada.length);
            }while(sorteio1 == sorteio2);
            Individuo filho = cruzamento(populacaoSelecionada[sorteio1],populacaoSelecionada[sorteio2]);
            populacaoNova[c] = filho;
        }
        aplicaMutacoesAleatoreas(0.1,populacao);
        //checando resultado-------------------------------------------------
        printDadosPopulacao(false);
        //------------------------------------------------------
        populacao = populacaoNova;//atualiza populacao
      }//fim do número de iterações
      printDadosPopulacao(true);//vendo resultado com melhor escala final
    }
    
    public void printDadosPopulacao(boolean imprimirMelhorEscala){
        System.out.println("==============================================================\n");
        System.out.println("Dados da população:");
        double media = 0;
        double maior = -1;
        int indiceMaior = -1;
        for(int c = 0; c < populacao.length; c++){
            media += calcularFe(populacao[c]);
            if(maior < calcularFe(populacao[c])){
                maior = calcularFe(populacao[c]);
                indiceMaior = c;
            }
        }
        media = media/populacao.length;
        System.out.println("media "+media);
        System.out.println("maior "+maior);
        if(imprimirMelhorEscala){
            printIndividuo(populacao[indiceMaior]);
        }
    }
    
    class Individuo{
        public int[] genes;
    }
    
    public Individuo cruzamento(Individuo progenitor1, Individuo progenitor2){
        Individuo filho = new Individuo();
        filho.genes = new int[progenitor1.genes.length];
        
        //ponto de corte
        int corte = progenitor1.genes.length/2;
        for(int c = 0; c < corte; c++){
            filho.genes[c] = progenitor1.genes[c];
        }
        for(int c = corte; c < progenitor2.genes.length; c++){
            filho.genes[c] = progenitor2.genes[c];
        }
        
        return filho;
    }
    
    //proporcao = proporção da população que sofrerá mutação, exemplo 0.1 = 10% da populacao
    public void aplicaMutacoesAleatoreas(double proporcao, Individuo[] populacao){
        int numeroMutacoes = (int) (populacao.length * proporcao);
        for(int c = 0; c < numeroMutacoes; c++){
            //sorteia individuo e realiza mutacao
            int sorteio = (int)(Math.random() * populacao.length);
            mutacao(populacao[sorteio]);
        }
    }
    
    public void mutacao(Individuo individuo){
        //sorteia gene a ser modificado
        int indiceGeneSorteado = (int)(Math.random() * individuo.genes.length);
        //sorteia Voo que irá substituir o vôo representando o Gene sorteado
        int indieceVooSelecionado = -1;
        do{
            indieceVooSelecionado = (int)(Math.random() * voos.length);                 
        }while(!clientes[indiceGeneSorteado].origem.contains(voos[indieceVooSelecionado].getOrigem()));
        
        individuo.genes[indiceGeneSorteado] = indieceVooSelecionado;
    }
    
    public void gerarPopulacao(int size){
        if(clientes == null || voos == null)
            System.err.println("precisa iniciar os vetores voos e clientes primeiro");
       
        populacao =  new Individuo[size];
        for(int c = 0; c < populacao.length; c++){
            Individuo ind = new Individuo();
            ind.genes = new int[clientes.length];
            for(int i = 0; i < clientes.length; i++){
                int sort  = -1;
                do{
                    sort  = (int)(Math.random()*(voos.length -1));
                }while(!clientes[i].origem.equals(voos[sort].origem));
                ind.genes[i] = sort;
            }
            populacao[c] = ind;
        } 
    }
    public double calcularFe(Individuo individuo){
        //cálculo do tempo de espera---------------------------------
        double tempoTotal = getTotalTempoEscala(individuo);
        //System.out.println("maior tempo total "+tempoTotal);
        
        //cálculo do valor total dos vôos---------------------------------
        double valorTotal = 0;
        for(int c = 0; c < individuo.genes.length; c++){
            valorTotal += voos[individuo.genes[c]].getValor();
        }
        //converte o valor da fe para uma escala exponencial
        //para diferenciar melhor a aptidão dos indivíduos na roleta
        return Math.pow(3,(10000/tempoTotal+10000/valorTotal))*0.00000001;
    }
    public double getTotalTempoEscala(Individuo individuo){
        
        int idVooMaisCedo = 0; //para encontrar voo que chega mais cedo 
        int idVooMaisTarde = 0;//para encontrar voo que chega mais tarde 
        for(int c = 1; c < individuo.genes.length; c++){
            if(Voo.horaParaDouble(voos[individuo.genes[c]].horaChegada) < Voo.horaParaDouble(voos[idVooMaisCedo].horaChegada)){
                idVooMaisCedo = individuo.genes[c];
            }
            if(Voo.horaParaDouble(voos[individuo.genes[c]].horaChegada) > Voo.horaParaDouble(voos[idVooMaisTarde].horaChegada)){
                idVooMaisTarde = individuo.genes[c];
            }
        }
        double menorTempoChegada = Voo.horaParaDouble(voos[idVooMaisCedo].horaChegada);
        double maiorTempoChegada = Voo.horaParaDouble(voos[idVooMaisTarde].horaChegada);
        //System.out.println("tempo" + maiorTempoChegada + " - "+menorTempoChegada);
        double tempoTotal = maiorTempoChegada - menorTempoChegada;
        return tempoTotal;
    }
    public double[] montarRoleta(Individuo[] populacao){
        double[] aptidoes = new double[populacao.length];
        double totalFe = 0;
        for(int i = 0; i < populacao.length; i++){
            aptidoes[i] = calcularFe(populacao[i]);
            totalFe += aptidoes[i];
        }
        //Monta a roleta
        double[] roleta = new double[populacao.length];
        for(int i = 0; i < populacao.length; i++){
            roleta[i] = aptidoes[i]/totalFe;
        }
        return roleta;
    }
    
    public Individuo[] selecaoNatural(int numSobreviventes, double[] roleta){
        Individuo[] sobreviventes = new Individuo[numSobreviventes];
        boolean[] sobreviventesInseridos = new boolean[populacao.length];
        for(int c = 0; c < numSobreviventes; c++){
            double sorteio = Math.random();
            double somaItemRoleta = 0;
            boolean sucesso = false;
            for(int i = 0; i < roleta.length; i++){
                somaItemRoleta += roleta[i]; 
                if(somaItemRoleta > sorteio && !sobreviventesInseridos[i]){
                    sobreviventes[c] = populacao[i];
                    sobreviventesInseridos[i] = true;
                    sucesso = true;
                    break;
                }           
            }
            if(!sucesso){
                c--;
            }
        }
        return sobreviventes;
    }
    public void printIndividuo(Individuo individuo){
        //mostra valor total e tempo total
        double valorTotal = 0;
        for(int c = 0; c < individuo.genes.length; c++)
            valorTotal += voos[individuo.genes[c]].getValor();
        //mostra o tempo total gasto
        double tempoTotal = getTotalTempoEscala(individuo);
        //formata tempo:
        int horas = (int) tempoTotal/60;
        int minutos = (int) tempoTotal%60;
        System.out.println("Valor total R$"+valorTotal);
        System.out.println("Tempo total "+horas+"h"+minutos+"min");
        //mostra escala completa
        System.out.println("--------\nEscala Completa:");
        for(int c = 0; c < individuo.genes.length; c++){
            System.out.println("Vôo do cliente "+c);
            System.out.println("Saída "+voos[individuo.genes[c]].getOrigem() + " - "+voos[individuo.genes[c]].getHoraSaida());
            System.out.println("Chegada "+voos[individuo.genes[c]].getDestino()+ " - "+voos[individuo.genes[c]].getHoraChegada());
            System.out.println("Tempo total de Vôo "+Voo.subtraiHoras(voos[individuo.genes[c]].getHoraChegada(), voos[individuo.genes[c]].getHoraSaida()));     
        }
    }
    
}

class Cliente{
    String nome;
    String origem;
    static String destino;
}

class Util{//esta classe serve para gerar voos e clientes para serem utilizados como entrada do algoritmo
    public static void gerarCodigoClientesAleatorios(int size){
      String nomes[] = new String[size];
      for(int i = 0; i < size; i++){
          char sortL1 = (char) ((int) (Math.random()*20) + 65);
          char sortL2 = (char) ((int) (Math.random()*20) + 65);
          nomes[i] = sortL1+ "" + sortL2;
      }
      //sorteia origem
      String origens[] = new String[size];
      for(int i = 0; i < size; i++){
          int sort = (int) (Math.random()*ProblemaViagensGrupoAlgoritmosGeneticos.aeroportos.length);
          origens[i] = ProblemaViagensGrupoAlgoritmosGeneticos.aeroportos[sort];
      }
      System.out.println("Cliente[] clientes = new Cliente["+size+"];");
      for(int i = 0; i < size; i++){
        StringBuilder saida = new StringBuilder("Cliente c"+i+" = new Cliente();\n");
        saida.append("c").append(i).append(".nome = \"").append(nomes[i]).append("\";\n");
        saida.append("c").append(i).append(".origem = \"").append(origens[i]).append("\";\n");
        saida.append("clientes[").append(i).append("] = c").append(i).append(";");
        System.out.println(saida);
      }
    }
    
    public static void gerarCodigoVoosAleatorios(int size, int destino/*id destino no vetor*/){
        //sorteira origem
        String origens[] = new String[size];
        for(int i = 0; i < size; i++){
            int sort = (int) (Math.random()*ProblemaViagensGrupoAlgoritmosGeneticos.aeroportos.length);
            if(sort == destino)
                sort = destino != 0 ? destino - 1 : 1; 
            origens[i] = ProblemaViagensGrupoAlgoritmosGeneticos.aeroportos[sort];
        } 
        //sorteira destino
        /*String destinos[] = new String[size];
        for(int i = 0; i < size; i++){
            int sort = (int) (Math.random()*ProblemaViagensGrupoAlgoritmosGeneticos.aeroportos.length);
            destinos[i] = ProblemaViagensGrupoAlgoritmosGeneticos.aeroportos[sort];
        }*/
        String destinos[] = new String[size];
        for(int i = 0; i < size; i++){
            destinos[i] = ProblemaViagensGrupoAlgoritmosGeneticos.aeroportos[destino];
        }
        //sorteira hora_saida
        String horaSaida[] = new String[size];
        for(int i = 0; i < size; i++){
            int ihora = (int) (Math.random()*12);
            int imin = (int) (Math.random()*59);
            String sImin = imin+"m";
            if(imin < 10)//para formatar minutos com 2 dígitos
                sImin = "0"+sImin;
            horaSaida[i] = ihora+"h"+sImin;
        }
        //sorteia hora_chegada
        String horaChegada[] = new String[size];
        for(int i = 0; i < size; i++){
            int ihora = (int) (Math.random()*12) + 12;
            int imin = (int) (Math.random()*59);
            String sImin = imin+"m";
            if(imin < 10)//para formatar minutos com 2 dígitos
                sImin = "0"+sImin;
            horaChegada[i] = ihora+"h"+sImin;
        }
        //sorteira valor
        double valores[] = new double[size];
        for(int i = 0; i < size; i++){
            valores[i] = Math.random()*2000;
        }
        System.out.println("Voo[] voos = new Voo["+size+"];");
        for(int i = 0; i < size; i++){
          StringBuilder saida = new StringBuilder("Voo v"+i+" = new Voo(");
          saida.append("\""+origens[i]+"\",");
          saida.append("\""+destinos[i]+"\",");
          saida.append("\""+horaSaida[i]+"\",");
          saida.append("\""+horaChegada[i]+"\",");
          saida.append(""+valores[i]+");\n");
          saida.append("voos[").append(i).append("] = v").append(i).append(";");
          System.out.println(saida);
        }
    }
}

class Voo{
    String origem;
    String destino;
    String horaSaida;//formato 00h00m
    String horaChegada;//formato 00h00m
    double valor;
    
    public Voo(String origem, String destino, String horaSaida, String horaChegada, double valor){
        this.origem = origem;
        this.destino = destino;
        this.horaChegada = horaChegada;
        this.horaSaida = horaSaida;
        this.valor = valor;
    }
    
    public void setOrigem(String origem){
        this.origem = origem;
    }
    public void setDestino(String destino){
        this.destino = destino;
    }
    public void setHoraSaida(String horaSaida){
        this.horaSaida = horaSaida;
    }
    public void setHoraChegada(String horaChegada){
        this.horaChegada = horaChegada;
    }
    public void setValor(double valor){
        this.valor = valor;
    }
    
    public String getOrigem(){
        return origem;
    }
    public String getDestino(){
        return destino;
    }
    public String getHoraSaida(){
        return horaSaida;
    }
    public String getHoraChegada(){
        return horaChegada;
    }
    public double getValor(){
        return valor;
    }
    
    public static double subtraiHoras(String hora1, String hora2){//formato 00h00m
        double dh1 = horaParaDouble(hora1);
        double dh2 = horaParaDouble(hora2);
        return dh1 - dh2;
    }
    public static double horaParaDouble(String hora){//formato 00h00m
        String[] hs = hora.split("h");
        double dh = Double.parseDouble(hs[0]);
        dh *= 60;
        double mh = Double.parseDouble(hs[1].substring(0, 2));
        return dh + mh;
    }
}

class Dados{
    //gerado automaticamente pelo método gerarVoosAleatorios
    public static Voo[] getVoos() {
        Voo[] voos = new Voo[80];
        Voo v0 = new Voo("XZF","GWQ","7h04m","21h43m",1770.1109222864527);
        voos[0] = v0;
        Voo v1 = new Voo("XZF","GWQ","3h51m","19h42m",265.3157050242361);
        voos[1] = v1;
        Voo v2 = new Voo("SAD","GWQ","7h39m","17h13m",1596.6813830665153);
        voos[2] = v2;
        Voo v3 = new Voo("GHI","GWQ","10h27m","19h51m",446.4833047395562);
        voos[3] = v3;
        Voo v4 = new Voo("DDI","GWQ","10h30m","23h51m",132.7178799159694);
        voos[4] = v4;
        Voo v5 = new Voo("FFW","GWQ","7h20m","15h20m",470.1787658746894);
        voos[5] = v5;
        Voo v6 = new Voo("KAI","GWQ","11h10m","13h30m",1641.282532013818);
        voos[6] = v6;
        Voo v7 = new Voo("GHI","GWQ","10h09m","21h30m",887.5997424579367);
        voos[7] = v7;
        Voo v8 = new Voo("XYZ","GWQ","9h19m","23h49m",1962.230564771674);
        voos[8] = v8;
        Voo v9 = new Voo("VBN","GWQ","11h18m","17h39m",1744.2182839529808);
        voos[9] = v9;
        Voo v10 = new Voo("KAI","GWQ","11h10m","12h47m",1811.6434694073125);
        voos[10] = v10;
        Voo v11 = new Voo("BDE","GWQ","9h20m","17h48m",1830.9068413142395);
        voos[11] = v11;
        Voo v12 = new Voo("XYZ","GWQ","10h21m","23h07m",514.2213191633163);
        voos[12] = v12;
        Voo v13 = new Voo("PSS","GWQ","7h21m","18h56m",958.5920011216335);
        voos[13] = v13;
        Voo v14 = new Voo("VBN","GWQ","9h46m","19h00m",122.46161881045082);
        voos[14] = v14;
        Voo v15 = new Voo("XZF","GWQ","5h27m","21h46m",1206.4053169370043);
        voos[15] = v15;
        Voo v16 = new Voo("KAI","GWQ","6h37m","18h25m",1594.5991003562053);
        voos[16] = v16;
        Voo v17 = new Voo("BDE","GWQ","4h47m","22h38m",1955.798141515081);
        voos[17] = v17;
        Voo v18 = new Voo("DDI","GWQ","11h54m","16h33m",1253.5821567380797);
        voos[18] = v18;
        Voo v19 = new Voo("SAD","GWQ","10h39m","14h46m",74.18050206584326);
        voos[19] = v19;
        Voo v20 = new Voo("XZF","GWQ","4h38m","13h29m",415.01782087943104);
        voos[20] = v20;
        Voo v21 = new Voo("XYZ","GWQ","6h17m","14h03m",587.6648959820252);
        voos[21] = v21;
        Voo v22 = new Voo("JJJ","GWQ","2h23m","13h06m",1167.484399554254);
        voos[22] = v22;
        Voo v23 = new Voo("SAD","GWQ","10h42m","18h32m",1601.0219440174496);
        voos[23] = v23;
        Voo v24 = new Voo("XYZ","GWQ","5h15m","12h07m",659.5900121832781);
        voos[24] = v24;
        Voo v25 = new Voo("PSS","GWQ","7h54m","21h39m",882.8129962081739);
        voos[25] = v25;
        Voo v26 = new Voo("DRR","GWQ","1h09m","20h45m",1771.6004215994872);
        voos[26] = v26;
        Voo v27 = new Voo("DDI","GWQ","6h51m","15h35m",1514.935294689695);
        voos[27] = v27;
        Voo v28 = new Voo("DDI","GWQ","5h02m","15h43m",133.5730791829346);
        voos[28] = v28;
        Voo v29 = new Voo("DDI","GWQ","4h52m","12h14m",169.82374706513605);
        voos[29] = v29;
        Voo v30 = new Voo("VBN","GWQ","8h47m","15h03m",536.1719121868531);
        voos[30] = v30;
        Voo v31 = new Voo("XZF","GWQ","4h29m","18h11m",1067.8802862702041);
        voos[31] = v31;
        Voo v32 = new Voo("ABC","GWQ","3h17m","21h05m",835.3967993590401);
        voos[32] = v32;
        Voo v33 = new Voo("SAD","GWQ","9h57m","16h30m",406.17366932185115);
        voos[33] = v33;
        Voo v34 = new Voo("VBN","GWQ","4h29m","13h26m",480.30299911894804);
        voos[34] = v34;
        Voo v35 = new Voo("GHI","GWQ","11h43m","21h41m",1242.1478560224248);
        voos[35] = v35;
        Voo v36 = new Voo("ABC","GWQ","3h45m","14h54m",1326.6057914624819);
        voos[36] = v36;
        Voo v37 = new Voo("XZF","GWQ","11h52m","19h06m",1449.3251194074028);
        voos[37] = v37;
        Voo v38 = new Voo("DRR","GWQ","2h49m","16h17m",3.3217693023732675);
        voos[38] = v38;
        Voo v39 = new Voo("KAI","GWQ","0h49m","12h23m",703.3668584873951);
        voos[39] = v39;
        Voo v40 = new Voo("JJJ","GWQ","9h57m","22h35m",1869.9919551082314);
        voos[40] = v40;
        Voo v41 = new Voo("GHI","GWQ","0h22m","16h07m",1555.190077242399);
        voos[41] = v41;
        Voo v42 = new Voo("FFW","GWQ","5h06m","17h23m",1665.5147079436372);
        voos[42] = v42;
        Voo v43 = new Voo("FFW","GWQ","6h45m","22h52m",1833.2097104302302);
        voos[43] = v43;
        Voo v44 = new Voo("DDI","GWQ","5h34m","12h07m",1964.3966354075594);
        voos[44] = v44;
        Voo v45 = new Voo("DRR","GWQ","3h50m","17h23m",1000.5121444927843);
        voos[45] = v45;
        Voo v46 = new Voo("DDI","GWQ","8h17m","22h36m",211.7526940018053);
        voos[46] = v46;
        Voo v47 = new Voo("VBN","GWQ","5h08m","23h42m",1067.4428785748948);
        voos[47] = v47;
        Voo v48 = new Voo("QRT","GWQ","7h14m","14h57m",1625.364058978055);
        voos[48] = v48;
        Voo v49 = new Voo("GHI","GWQ","6h42m","21h33m",399.54101504949114);
        voos[49] = v49;
        Voo v50 = new Voo("FFW","GWQ","8h28m","14h23m",883.4794050494326);
        voos[50] = v50;
        Voo v51 = new Voo("QRT","GWQ","1h42m","13h55m",1720.0488395153834);
        voos[51] = v51;
        Voo v52 = new Voo("XYZ","GWQ","2h01m","23h41m",44.03172399987065);
        voos[52] = v52;
        Voo v53 = new Voo("VBN","GWQ","3h45m","15h10m",21.70048220480347);
        voos[53] = v53;
        Voo v54 = new Voo("XZF","GWQ","4h37m","23h46m",1378.986545930616);
        voos[54] = v54;
        Voo v55 = new Voo("DRR","GWQ","8h10m","14h25m",1491.7210611903001);
        voos[55] = v55;
        Voo v56 = new Voo("KAI","GWQ","2h03m","22h18m",102.3457351294188);
        voos[56] = v56;
        Voo v57 = new Voo("GHI","GWQ","4h14m","23h36m",215.77987342771942);
        voos[57] = v57;
        Voo v58 = new Voo("JJJ","GWQ","8h04m","20h33m",698.8533450571357);
        voos[58] = v58;
        Voo v59 = new Voo("PSS","GWQ","0h24m","12h40m",941.114984320194);
        voos[59] = v59;
        Voo v60 = new Voo("ABC","GWQ","9h02m","16h36m",630.8931005146752);
        voos[60] = v60;
        Voo v61 = new Voo("ABC","GWQ","4h28m","23h41m",1907.5071802432203);
        voos[61] = v61;
        Voo v62 = new Voo("DDI","GWQ","5h40m","22h29m",1552.8808558970359);
        voos[62] = v62;
        Voo v63 = new Voo("PSS","GWQ","6h01m","17h58m",1339.8130158272252);
        voos[63] = v63;
        Voo v64 = new Voo("SAD","GWQ","8h05m","15h26m",266.3449381820713);
        voos[64] = v64;
        Voo v65 = new Voo("XZF","GWQ","8h14m","17h58m",61.18921322680904);
        voos[65] = v65;
        Voo v66 = new Voo("BDE","GWQ","4h16m","21h52m",6.804908196245796);
        voos[66] = v66;
        Voo v67 = new Voo("JJJ","GWQ","9h37m","22h14m",537.0421650184645);
        voos[67] = v67;
        Voo v68 = new Voo("PSS","GWQ","4h44m","15h08m",1298.7090218459684);
        voos[68] = v68;
        Voo v69 = new Voo("XZF","GWQ","5h06m","16h18m",1982.4339434541573);
        voos[69] = v69;
        Voo v70 = new Voo("QRT","GWQ","2h03m","15h48m",611.5239710470332);
        voos[70] = v70;
        Voo v71 = new Voo("DRR","GWQ","2h02m","21h57m",1727.7596204741437);
        voos[71] = v71;
        Voo v72 = new Voo("XYZ","GWQ","5h07m","20h44m",994.2920355307452);
        voos[72] = v72;
        Voo v73 = new Voo("FFW","GWQ","1h39m","15h01m",387.7961757806303);
        voos[73] = v73;
        Voo v74 = new Voo("QRT","GWQ","9h26m","18h48m",1384.5330857396552);
        voos[74] = v74;
        Voo v75 = new Voo("VBN","GWQ","11h31m","13h25m",347.8000627910707);
        voos[75] = v75;
        Voo v76 = new Voo("PSS","GWQ","6h36m","13h49m",1538.475921652609);
        voos[76] = v76;
        Voo v77 = new Voo("ABC","GWQ","6h06m","20h57m",278.51797210081486);
        voos[77] = v77;
        Voo v78 = new Voo("PSS","GWQ","5h45m","12h54m",702.7051882434736);
        voos[78] = v78;
        Voo v79 = new Voo("XYZ","GWQ","7h41m","22h24m",712.3569282405368);
        voos[79] = v79;
        return voos;
    }
    //gerado automaticamente pelo método gerarClientesAleatorios
    public static Cliente[] getClientes(){
        Cliente[] clientes = new Cliente[20];
        Cliente c0 = new Cliente();
        c0.nome = "QL";
        c0.origem = "SAD";
        clientes[0] = c0;
        Cliente c1 = new Cliente();
        c1.nome = "NR";
        c1.origem = "GHI";
        clientes[1] = c1;
        Cliente c2 = new Cliente();
        c2.nome = "CL";
        c2.origem = "DRR";
        clientes[2] = c2;
        Cliente c3 = new Cliente();
        c3.nome = "NQ";
        c3.origem = "DDI";
        clientes[3] = c3;
        Cliente c4 = new Cliente();
        c4.nome = "DG";
        c4.origem = "DRR";
        clientes[4] = c4;
        Cliente c5 = new Cliente();
        c5.nome = "MB";
        c5.origem = "BDE";
        clientes[5] = c5;
        Cliente c6 = new Cliente();
        c6.nome = "JN";
        c6.origem = "SAD";
        clientes[6] = c6;
        Cliente c7 = new Cliente();
        c7.nome = "HN";
        c7.origem = "XYZ";
        clientes[7] = c7;
        Cliente c8 = new Cliente();
        c8.nome = "RO";
        c8.origem = "SAD";
        clientes[8] = c8;
        Cliente c9 = new Cliente();
        c9.nome = "FQ";
        c9.origem = "XZF";
        clientes[9] = c9;
        Cliente c10 = new Cliente();
        c10.nome = "HL";
        c10.origem = "DDI";
        clientes[10] = c10;
        Cliente c11 = new Cliente();
        c11.nome = "KS";
        c11.origem = "QRT";
        clientes[11] = c11;
        Cliente c12 = new Cliente();
        c12.nome = "PA";
        c12.origem = "GHI";
        clientes[12] = c12;
        Cliente c13 = new Cliente();
        c13.nome = "ME";
        c13.origem = "KAI";
        clientes[13] = c13;
        Cliente c14 = new Cliente();
        c14.nome = "OL";
        c14.origem = "FFW";
        clientes[14] = c14;
        Cliente c15 = new Cliente();
        c15.nome = "NS";
        c15.origem = "XYZ";
        clientes[15] = c15;
        Cliente c16 = new Cliente();
        c16.nome = "JG";
        c16.origem = "DDI";
        clientes[16] = c16;
        Cliente c17 = new Cliente();
        c17.nome = "JJ";
        c17.origem = "VBN";
        clientes[17] = c17;
        Cliente c18 = new Cliente();
        c18.nome = "JF";
        c18.origem = "JJJ";
        clientes[18] = c18;
        Cliente c19 = new Cliente();
        c19.nome = "QS";
        c19.origem = "DDI";
        clientes[19] = c19;
        return clientes;
    }

}



