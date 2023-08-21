package com.caracrazy;

import autoitx4java.AutoItX;
import com.caracrazy.automation.Automatizador;
import com.caracrazy.automation.AutomatizadorAutoItX;
import com.caracrazy.automation.AutomatizadorJna;
import com.caracrazy.automation.CmdController;
import com.caracrazy.automation.autoit.AutoItXData;
import com.caracrazy.automation.autoit.AutoItXFactory;
import com.caracrazy.configuration.ConfigurationFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

import static com.caracrazy.CustomAssertions.assertAll;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class AutomatizadorIT {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        Collection<Object[]> result = new ArrayList<>();
        result.add(new Object[]{ createAutoIt(), "Edit1" });
        result.add(new Object[]{ new AutomatizadorJna(), "Edit" });
        return result;
    }

    public static Automatizador createAutoIt() {
        AutoItXData config = ConfigurationFactory.create("config.yaml").getAutomator().getAutoItX();
        AutoItX autoItX = AutoItXFactory.INSTANCE.create(config);
        return new AutomatizadorAutoItX(autoItX);
    }

    private final Automatizador automatizador;
    private final String controlId;

    public AutomatizadorIT(Automatizador automatizador, String controlId) {
        this.automatizador = automatizador;
        this.controlId = controlId;
    }

    private static final String notepadTitle =
            "[REGEXPTITLE:(?i)(.*Bloco de Notas.*|.*Notepad.*)]";

    private static final String paintTitle =
            "[REGEXPTITLE:(?i)(.*Paint.*)]";

    private static final String notepad =
            "notepad.exe";

    private static final String mspaint =
            "mspaint.exe";

    @Before
    @After
    public void clearApps() {
        automatizador.closeAllProcesses(notepad);
        automatizador.closeAllProcesses(mspaint);
    }

    private static void abrirApp(String nome) {
        abrirApp(nome, 1);
    }
    private static void abrirApp(String nome, int quantidade) {
        long processosAntes = CmdController.INSTANCE.contarProcessos(nome);
        assumeTrue(
                "Não deve haver nenhum processo ativo da aplicação '" + nome + "'",
                processosAntes == 0
        );

        for (int i = 0; i < quantidade; i++) {
            CmdController.INSTANCE.abrirUnicaInstancia("C:\\windows\\system32", nome);
        }

        long processosDepois = CmdController.INSTANCE.contarProcessos(nome);
        assumeTrue(
                "Devem haver '" + quantidade + "' processos da aplicação do tipo '" + nome + "'",
                processosDepois == quantidade
        );
    }

    @Test
    public void winGetHandle_deve_retornar_um_handle_valido() {
        // DADO
        abrirApp(notepad);

        // QUANDO
        String resultado = automatizador.winGetHandle(notepadTitle);

        // ENTAO
        Assert.assertNotEquals("", resultado);
    }

    @Test
    public void fechar_todos_os_processos_dado_um_nome() {
        // DADO
        int quantidadeDeProcessos = 3;
        abrirApp(notepad, quantidadeDeProcessos);

        // QUANDO
        automatizador.closeAllProcesses(notepad);

        // ENTAO
        Assert.assertEquals(0, CmdController.INSTANCE.contarProcessos(notepad));
    }

    @Test
    public void fechar_processo_que_nao_existe() {
        // DADO
        String processoInexistente = "processoInexistente";
        assumeTrue(
                "Não deve existir nenhum processo chamado '" + processoInexistente + "'",
                CmdController.INSTANCE.contarProcessos(processoInexistente) == 0
        );

        // QUANDO
        Runnable acao = () -> automatizador.closeAllProcesses(processoInexistente);

        // ENTAO

        try {
            acao.run();
        } catch (Throwable e) {
            throw new AssertionError("Não deveria lançar nenhum exception", e);
        }
    }

    @Test
    public void winActivate_deve_ativar_uma_janela_e_winWaitActive_deve_retornar_true_apos_janela_ser_focada() {
        // DADO
        abrirApp(notepad);
        abrirApp(mspaint);
        automatizador.winWaitActive(paintTitle, "", 5);

        // QUANDO
        boolean antes = automatizador.winWaitActive(notepadTitle, "", 5);
        automatizador.winActivate(notepadTitle);
        boolean depois = automatizador.winWaitActive(notepadTitle, "", 5);

        // ENTAO
        assertAll(
                () -> assertFalse("Antes winWaitActive deveria ser false", antes),
                () -> assertTrue("Depois winWaitActive deveria ser true", depois)
        );
    }

    @Test
    public void winGetPosX_deve_retornar_um_valor_positivo() {
        // DADO
        abrirApp(notepad);

        // QUANDO
        int posicao = automatizador.winGetPosX(notepadTitle);

        // ENTAO
        assertTrue("posição X do bloco de notas não deve ser negativa",posicao >= 0);
    }

    @Test
    public void winGetPosY_deve_retornar_um_valor_positivo() {
        // DADO
        abrirApp(notepad);

        // QUANDO
        int posicao = automatizador.winGetPosY(notepadTitle);

        // ENTAO
        assertTrue("posição Y do bloco de notas não deve ser negativa",posicao >= 0);
    }

    @Test
    public void controlGetPosX_deve_ser_zero_pois_esta_alinhado_a_margem_esquerda_do_aplicativo() {
        // DADO
        abrirApp(notepad);

        // QUANDO
        int controlX = automatizador.controlGetPosX(notepadTitle, "", controlId);

        // ENTAO
        assertEquals(
                "O elemento '" + controlId + "' deve ter sua posição" +
                " no exio x exatamente igual a zero, pois este elemento está" +
                " alinhado com a margem esquerda do notepad.",
                0,
                controlX
        );
    }

    @Test
    public void controlGetPosY_deve_ser_zero_pois_esta_alinhado_a_margem_superior_do_aplicativo() {
        // DADO
        abrirApp(notepad);

        // QUANDO
        int controlY = automatizador.controlGetPosY(notepadTitle, "", controlId);

        // ENTAO
        assertEquals(
                "O elemento '" + controlId + "' deve ter sua posição" +
                " no exio y exatamente igual a zero, pois este elemento está" +
                " alinhado com a margem superior do notepad.",
                0,
                controlY
        );
    }

    @Test
    public void controlGetPosWidth_deve_ser_igual_ao_tamanho_da_janela_mais_um_valor_fixo() {
        // DADO
        abrirApp(notepad);
        int larguraDaBarraDeRolagem = 16;
        int tamanhoEsperado = automatizador.winGetPosWidth(notepadTitle) - larguraDaBarraDeRolagem;

        // QUANDO
        int tamanhoObtido = automatizador.controlGetPosWidth(notepadTitle, "", controlId);

        // ENTAO
        assertEquals(
                "O elemento '" + controlId + "' deve se extender em" +
                " largura do começo da janela do notepad até a barra de" +
                " rolagem da direita, ou seja, o elemento '" + controlId +
                "' deve ter exatamente a mesa largura da janela menos 16" +
                " pixels (Que é a largura da barra de rolagem).",
                tamanhoEsperado,
                tamanhoObtido
        );
    }

    @Test
    public void controlGetPosHeight_deve_ser_igual_ao_tamanho_da_janela_mais_um_valor_fixo() {
        // DADO
        abrirApp(notepad);
        int tamanhoDosMenus = 82;
        int tamanhoEsperado = automatizador.winGetPosHeight(notepadTitle) - tamanhoDosMenus;

        // QUANDO
        int tamanhoObtido = automatizador.controlGetPosHeight(notepadTitle, "", controlId);

        // ENTAO
        assertEquals(
                "O elemento '" + controlId + "' deve ocupar em altura" +
                " praticamente toda a janela do notepad, descontando a altura" +
                " dos menus, barra de rolagem e barra de status.",
                tamanhoEsperado,
                tamanhoObtido
        );
    }

    @Test
    public void processExists_deve_ser_um_pid_diferente_de_zero_e_positivo() {
        // DADO
        abrirApp(notepad);

        // QUANDO
        int pid = automatizador.processExists(notepad);

        // ENTAO
        assertTrue("Pid do processo deve ser diferente de zero", pid > 0);
    }

    @Test
    public void processClose_deve_fechar_um_processo_existente() {
        // DADO
        abrirApp(notepad);
        int pid = automatizador.processExists(notepad);
        assumeTrue(
                "Aparentemente nenhum do tipo '" + notepad + "' foi encontrado.",
                pid > 0
        );

        // QUANDO
        automatizador.processClose(notepad);

        // ENTAO
        boolean resultado = automatizador.processExists(notepad) == 0;
        long tempoLimite = System.nanoTime() + 10_000_000;
        while (!resultado || System.nanoTime() < tempoLimite) {
            resultado = automatizador.processExists(notepad) == 0;
        }

        assertTrue(
                "pid do processo deve retornar zero, com um tempo de" +
                        " tolerância de 10 segundos",
                resultado
        );
    }

    @Test
    public void mouseMove_deve_mover_cursor_do_mouse_e_ser_verificado_por_mouseGetPosX_e_mouseGetPosY() {
        // DADO
        Point pontoInicial = new Point(10, 12);
        Point pontoFinal = new Point(20, 24);
        automatizador.mouseMove(pontoInicial.x, pontoInicial.y, 0);
        assumeTrue(
                "A posição inicial do mouse deveria ser " + pontoInicial,
                new Point(automatizador.mouseGetPosX(), automatizador.mouseGetPosY()).equals(pontoInicial)
        );

        // QUANDO
        automatizador.mouseMove(pontoFinal.x, pontoFinal.y, 0);

        // ENTAO
        assertEquals(pontoFinal, new Point(automatizador.mouseGetPosX(), automatizador.mouseGetPosY()));
    }

    @Test
    public void mouseDown_seguido_de_mouseUp_deve_mover_a_janela_da_aplicacao() {
        // DADO
        abrirApp(notepad);
        Rectangle posicaoAntes = automatizador.getRect(notepadTitle);
        int recuoHorizontal = posicaoAntes.width / 2;
        int recuoVertical = 10;
        int movimentoHorizontal = 50;
        Rectangle posicaoEsperada = new Rectangle(
                posicaoAntes.x + movimentoHorizontal,
                posicaoAntes.y,
                posicaoAntes.width,
                posicaoAntes.height
        );

        // QUANDO
        automatizador.mouseMove(
                posicaoAntes.x + recuoHorizontal,
                posicaoAntes.y + recuoVertical,
                0
        );
        automatizador.mouseDown("left");
        automatizador.mouseMove(
                posicaoAntes.x + recuoHorizontal + movimentoHorizontal,
                posicaoAntes.y + recuoVertical,
                10
        );
        automatizador.mouseUp("left");

        // ENTAO
        Rectangle posicaoDepois = automatizador.getRect(notepadTitle);
        assertEquals(posicaoEsperada, posicaoDepois);
    }

    @Test
    public void mouseClickDrag_deve_mover_a_janela_da_aplicacao() {
        // DADO
        abrirApp(notepad);
        Rectangle posicaoAntes = automatizador.getRect(notepadTitle);
        int recuoHorizontal = posicaoAntes.width / 2;
        int recuoVertical = 10;
        int movimentoHorizontal = 10;
        Rectangle posicaoEsperada = new Rectangle(
                posicaoAntes.x + movimentoHorizontal,
                posicaoAntes.y,
                posicaoAntes.width,
                posicaoAntes.height
        );

        // QUANDO
        automatizador.mouseClickDrag(
                "left",
                posicaoAntes.x + recuoHorizontal,
                posicaoAntes.y + recuoVertical,
                posicaoAntes.x + recuoHorizontal + movimentoHorizontal,
                posicaoAntes.y + recuoVertical
        );

        // ENTAO
        Rectangle posicaoDepois = automatizador.getRect(notepadTitle);
        assertEquals(posicaoEsperada, posicaoDepois);
    }

    @Test
    public void controlClick_com_notepad_aberto_deve_retornar_true() {
        // DADO
        abrirApp(notepad);

        // QUANDO
        boolean resultado = automatizador.controlClick(notepadTitle, "", controlId);

        // ENTAO
        assertTrue(resultado);
    }

    @Test
    public void controlClick_com_notepad_fechado_deve_retornar_false() {
        // DADO
        // Não abrir o notepad

        // QUANDO
        boolean resultado = automatizador.controlClick(notepadTitle, "", controlId);

        // ENTAO
        assertFalse(resultado);
    }

    @Test
    public void controlClick_em_botao_com_notepad_fechado_deve_retornar_false() {
        // DADO
        // Não abrir o notepad

        // QUANDO
        boolean resultado = automatizador.controlClick(notepadTitle, "", controlId, "", 1, 0, 0);

        // ENTAO
        assertFalse(resultado);
    }

    @Test
    public void controlSend_deve_escrever_um_texto_no_notepad_e_controlGetText_deve_retornar_o_texto_escrito() throws InterruptedException {
        // DADO
        abrirApp(notepad);
        String textoEsperado = "Texto esperado";
        boolean isNotRaw = false;

        // QUANDO
        automatizador.controlSend(notepadTitle, "", controlId, textoEsperado, isNotRaw);
        String textoObtido = automatizador.controlGetText(notepadTitle, "", controlId);

        // ENTAO
        assertEquals(textoEsperado, textoObtido);
    }

    @Test
    public void winGetPosWidth_deve_retornar_valor_positivo_diferente_de_zero() {
        // DADO
        abrirApp(notepad);

        // QUANDO
        int largura = automatizador.winGetPosWidth(notepadTitle);

        // ENTAO
        assertTrue(largura > 0);
    }

    @Test
    public void winGetPosHeight_deve_retornar_valor_positivo_diferente_de_zero() {
        // DADO
        abrirApp(notepad);

        // QUANDO
        int altura = automatizador.winGetPosHeight(notepadTitle);

        // ENTAO
        assertTrue(altura > 0);
    }
}

