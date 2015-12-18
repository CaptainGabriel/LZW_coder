package LZWTokenizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro Gabriel
 * @for Sistemas Multimedia
 */
public class TokenizerLZW {
    private static ArrayList<File> allFiles = new ArrayList<>();
    private final int CAPACIDADE = 2048;
    private int tamanhoActual;
    private Map<String,Integer> dicionario;
    private char curChar;

    public TokenizerLZW(String... files) {
        for(String f : files)
            allFiles.add(new File(f));
        dicionario = asciArray();//init com 256 asci char's
        tamanhoActual = dicionario.size();
        curChar = ' ';
    }

    public void codificacaoGeral(){
        allFiles.forEach((File f)->{
            try {
                codificacao(f);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }



    private Map<String,Integer> asciArray(){
        Map<String,Integer> dic = new HashMap<>();
        for (int i = 32; i < 127; i++) {
                String K = ""+(char)i;
                Integer V = i;
            dic.put(K,V);
        }
        return dic;
    }

    /**
     * Baseado no fluxograma de "Engineers's Guide to Digital Signal Processing"
     * Se o dicionario já conhece a concatenacao
     * guarda-se a mesma e adiciona-se o proximo char.
     * Obtem-se o code do que já la estava
     * e adiciona-se a concatenação ao dicionario
     */
    private void codificacao(File f) throws IOException{
        String charStream = new String(Files.readAllBytes(f.toPath()));
        List<Integer> sequencia = new ArrayList<>();
        String aux = ""; //input first byte, store in string
        for(char c : charStream.toCharArray()){
            curChar = c;//input next byte, store in char
            String concat = curChar+aux;
            if(this.dicionario.containsKey(concat))//is string+char in table?
                aux=concat; //string=string+char
            else{
                sequencia.add(dicionario.get(aux));//output the code for string
                if(this.CAPACIDADE>=dicionario.size())
                    dicionario.put(concat, tamanhoActual++);//add entry to table
                else{//se exceder a capacidade, aplicar politica de gestao
                    writeFile(sequencia, f);
                    sequencia.clear();dicionario.clear();
                    dicionario=asciArray();
                }aux=curChar+"";//string=char
            }
        }
        System.out.println("Ficheiro "+f.getName());
        sequencia.forEach((Integer i)->{
            System.out.print(i+" ");
        });//output code for string
        writeFile(sequencia, f);
    }

    /**
     * Para uma sequencia, codifica em ficheiro.
     */
    private void writeFile(List<Integer> sequencia, File f){
        File newFile = new File("lzw"+f.getName());
        try(FileOutputStream out = new FileOutputStream(newFile,true)){
            sequencia.forEach((Integer i)->{
                try{
                    if(i != null && out != null)
                        out.write(i);
                }catch(IOException ex){
                    System.err.println(ex.getMessage());
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(TokenizerLZW.class.getName())
                    .log(Level.SEVERE, null, ex.getMessage());
        }
    }

    public static void main(String[] args) {
        TokenizerLZW coderLZW = new TokenizerLZW(args);
        coderLZW.codificacaoGeral();
     }


}
