package br.edu.ifsuldeminas.sd.chat.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

/**
 * Interface Gráfica moderna para o Chat UDP.
 * Implementa MessageContainer conforme os requisitos da atividade.
 */
public class ChatSwingUI extends JFrame implements MessageContainer {
	private static final long serialVersionUID = 1L;

	// Definição da paleta de cores (Tema Dark Moderno / Catppuccin Mocha inspirado)
	private static final Color COLOR_BG = new Color(30, 30, 46);           // #1e1e2e
	private static final Color COLOR_SURFACE = new Color(37, 37, 56);      // #252538
	private static final Color COLOR_FIELD = new Color(49, 50, 68);        // #313244
	private static final Color COLOR_BORDER = new Color(69, 71, 90);       // #45475a
	private static final Color COLOR_TEXT = new Color(205, 214, 244);      // #cdd6f4
	private static final Color COLOR_TEXT_MUTED = new Color(166, 173, 186); // #a6adba
	private static final Color COLOR_PRIMARY = new Color(203, 166, 247);   // #cba6f7 (Purple/Lilac)
	private static final Color COLOR_PRIMARY_HOVER = new Color(224, 204, 250); // Lighter lilac
	private static final Color COLOR_GREEN = new Color(166, 227, 161);     // #a6e3a1
	private static final Color COLOR_RED = new Color(243, 139, 168);       // #f38ba8

	// Componentes de Conexão
	private JTextField localPortField;
	private JTextField remoteIpField;
	private JTextField remotePortField;
	private JTextField nicknameField;
	private JButton startButton;
	private JLabel statusLabel;
	private JPanel statusIndicator;

	// Componentes de Mensagens
	private JTextPane chatTextPane;
	private JTextField messageInputField;
	private JButton sendButton;
	private JButton clearButton;

	// Estado e API
	private Sender sender;
	private boolean isChatActive = false;

	public ChatSwingUI() {
		// Configurações básicas da janela principal
		setTitle("UDP Messenger");
		setSize(550, 650);
		setMinimumSize(new Dimension(450, 550));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Centraliza na tela
		getContentPane().setBackground(COLOR_BG);
		setLayout(new BorderLayout(0, 0));

		// Inicializar Componentes de UI
		createConfigPanel();
		createChatArea();
		createMessageInputPanel();

		// Inicia com foco no campo de porta local
		SwingUtilities.invokeLater(() -> localPortField.requestFocusInWindow());
	}

	/**
	 * Cria a seção superior de configurações da conexão UDP.
	 */
	private void createConfigPanel() {
		JPanel topContainer = new JPanel(new BorderLayout());
		topContainer.setBackground(COLOR_SURFACE);
		topContainer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

		// Painel de formulário usando GridBagLayout para alinhamento perfeito
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(COLOR_SURFACE);
		formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 8, 4, 8);

		// Linha 1: Apelido e Porta Local
		JLabel lblNickname = new JLabel("Seu Apelido:");
		styleLabel(lblNickname);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.1;
		formPanel.add(lblNickname, gbc);

		nicknameField = new JTextField("Usuário");
		styleField(nicknameField);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		formPanel.add(nicknameField, gbc);

		JLabel lblLocalPort = new JLabel("Porta Local:");
		styleLabel(lblLocalPort);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 0.1;
		formPanel.add(lblLocalPort, gbc);

		localPortField = new JTextField("12345");
		styleField(localPortField);
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		formPanel.add(localPortField, gbc);

		// Linha 2: IP Remoto e Porta Remota
		JLabel lblRemoteIp = new JLabel("IP Remoto:");
		styleLabel(lblRemoteIp);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0.1;
		formPanel.add(lblRemoteIp, gbc);

		remoteIpField = new JTextField("localhost");
		styleField(remoteIpField);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0.4;
		formPanel.add(remoteIpField, gbc);

		JLabel lblRemotePort = new JLabel("Porta Remota:");
		styleLabel(lblRemotePort);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 0.1;
		formPanel.add(lblRemotePort, gbc);

		remotePortField = new JTextField("12346");
		styleField(remotePortField);
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.weightx = 0.4;
		formPanel.add(remotePortField, gbc);

		// Botão Iniciar Conexão
		startButton = new JButton("Iniciar Chat");
		styleButton(startButton, COLOR_PRIMARY, new Color(17, 17, 27), COLOR_PRIMARY_HOVER);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startChat();
			}
		});

		// Adiciona o botão esticando pelas colunas da direita na próxima linha
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 4;
		gbc.insets = new Insets(12, 8, 4, 8);
		formPanel.add(startButton, gbc);

		// Painel de Status da Conexão
		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		statusPanel.setBackground(COLOR_SURFACE);
		statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 23, 12, 15));

		statusIndicator = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(isChatActive ? COLOR_GREEN : COLOR_RED);
				g2d.fillOval(0, 0, 10, 10);
				g2d.dispose();
			}
		};
		statusIndicator.setPreferredSize(new Dimension(10, 10));
		statusIndicator.setBackground(COLOR_SURFACE);

		statusLabel = new JLabel("Status: Inativo");
		statusLabel.setForeground(COLOR_TEXT_MUTED);
		statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

		statusPanel.add(statusIndicator);
		statusPanel.add(statusLabel);

		topContainer.add(formPanel, BorderLayout.CENTER);
		topContainer.add(statusPanel, BorderLayout.SOUTH);

		add(topContainer, BorderLayout.NORTH);
	}

	/**
	 * Cria a área central onde as mensagens de texto serão exibidas.
	 */
	private void createChatArea() {
		chatTextPane = new JTextPane();
		chatTextPane.setContentType("text/html");
		chatTextPane.setEditable(false);
		chatTextPane.setBackground(COLOR_BG);
		chatTextPane.setCaretColor(COLOR_BG); // Oculta o cursor piscando ao clicar

		// Configuração de estilos globais via StyleSheet
		HTMLDocument doc = (HTMLDocument) chatTextPane.getDocument();
		StyleSheet styleSheet = doc.getStyleSheet();
		styleSheet.addRule("body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #1e1e2e; color: #cdd6f4; margin: 12px; }");
		styleSheet.addRule(".sent-box { text-align: right; margin-bottom: 6px; }");
		styleSheet.addRule(".received-box { text-align: left; margin-bottom: 6px; }");
		styleSheet.addRule(".sent-meta { color: #cba6f7; font-weight: bold; font-size: 11px; }");
		styleSheet.addRule(".received-meta { color: #f9e2af; font-weight: bold; font-size: 11px; }");
		styleSheet.addRule(".time { color: #6c7086; font-size: 9px; font-weight: normal; }");
		styleSheet.addRule(".msg-body { font-size: 12px; color: #cdd6f4; display: inline-block; text-align: left; }");

		JScrollPane scrollPane = new JScrollPane(chatTextPane);
		scrollPane.setBackground(COLOR_BG);
		scrollPane.getViewport().setBackground(COLOR_BG);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Cria a área inferior para escrita e envio de mensagens.
	 */
	private void createMessageInputPanel() {
		JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
		bottomPanel.setBackground(COLOR_BG);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

		clearButton = new JButton("Limpar");
		styleButton(clearButton, COLOR_SURFACE, COLOR_TEXT, COLOR_FIELD);
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearChat();
			}
		});

		messageInputField = new JTextField();
		styleField(messageInputField);
		messageInputField.setEnabled(false);
		messageInputField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendCurrentMessage();
				}
			}
		});

		sendButton = new JButton("Enviar");
		styleButton(sendButton, COLOR_PRIMARY, new Color(17, 17, 27), COLOR_PRIMARY_HOVER);
		sendButton.setEnabled(false);
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendCurrentMessage();
			}
		});

		bottomPanel.add(clearButton, BorderLayout.WEST);
		bottomPanel.add(messageInputField, BorderLayout.CENTER);
		bottomPanel.add(sendButton, BorderLayout.EAST);

		add(bottomPanel, BorderLayout.SOUTH);
	}

	/**
	 * Lógica de inicialização do Chat UDP por meio da API do professor.
	 */
	private void startChat() {
		String localPortStr = localPortField.getText().trim();
		String remoteIp = remoteIpField.getText().trim();
		String remotePortStr = remotePortField.getText().trim();
		String nickname = nicknameField.getText().trim();

		// Validações
		if (nickname.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, defina um apelido.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int localPort;
		try {
			localPort = Integer.parseInt(localPortStr);
			if (localPort <= 1024 || localPort > 65535) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "A porta local deve ser um número inteiro entre 1025 e 65535.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int remotePort;
		try {
			remotePort = Integer.parseInt(remotePortStr);
			if (remotePort <= 0 || remotePort > 65535) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "A porta remota deve ser um número inteiro entre 1 e 65535.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (remoteIp.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, informe o IP remoto.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			statusLabel.setText("Status: Conectando...");
			statusIndicator.repaint();

			// Constrói o sender e inicia o receiver integrado (passando esta view como MessageContainer)
			sender = ChatFactory.build(remoteIp, remotePort, localPort, this);

			// Atualiza estado de conexão ativa
			isChatActive = true;
			statusLabel.setText("Status: Ativo");
			statusIndicator.repaint();

			// Bloqueia alterações de configuração para evitar conflito de socket
			localPortField.setEditable(false);
			remoteIpField.setEditable(false);
			remotePortField.setEditable(false);
			nicknameField.setEditable(false);
			startButton.setEnabled(false);

			// Habilita controles de envio
			messageInputField.setEnabled(true);
			sendButton.setEnabled(true);
			messageInputField.requestFocusInWindow();

		} catch (ChatException chatException) {
			statusLabel.setText("Status: Erro");
			isChatActive = false;
			statusIndicator.repaint();

			String causeMessage = (chatException.getCause() != null) ? chatException.getCause().getMessage() : chatException.getMessage();
			JOptionPane.showMessageDialog(this, "Não foi possível abrir o socket UDP:\n" + causeMessage, "Erro no Chat UDP", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Lógica de envio da mensagem digitada.
	 */
	private void sendCurrentMessage() {
		if (!isChatActive || sender == null) {
			return;
		}

		String text = messageInputField.getText().trim();
		if (text.isEmpty()) {
			return;
		}

		String fromName = nicknameField.getText().trim();
		if (fromName.isEmpty()) {
			fromName = "Usuário";
		}

		// Formata o payload de acordo com a regra da API (conteudo + separador + apelido)
		String payload = String.format("%s%s%s", text, MessageContainer.FROM, fromName);

		try {
			sender.send(payload);

			// Adiciona mensagem localmente no chat com alinhamento à direita
			String timeStr = new SimpleDateFormat("HH:mm:ss").format(new Date());
			String html = String.format(
					"<div class='sent-box'>" +
							"  <span class='sent-meta'>Você</span> " +
							"  <span class='time'>[%s]</span><br/>" +
							"  <span class='msg-body'>%s</span>" +
							"</div>" +
							"<div style='margin-bottom: 8px;'></div>",
					timeStr, escapeHTML(text));

			appendHTML(html);

			// Limpa e foca novamente
			messageInputField.setText("");
			messageInputField.requestFocusInWindow();

		} catch (ChatException e) {
			JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Callback implementado da interface MessageContainer.
	 * Chamado pela thread paralela da API do Chat (UDPReceiver).
	 */
	@Override
	public void newMessage(final String message) {
		// Garante a execução na thread de despacho de eventos do Swing (Thread Safety)
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				handleIncomingMessage(message);
			}
		});
	}

	/**
	 * Recebe e processa o texto bruto recebido do UDP socket.
	 */
	private void handleIncomingMessage(String message) {
		if (message == null) {
			return;
		}

		// Remove bytes nulos comuns no final do buffer UDP e faz trim
		String cleanMsg = message.replace("\u0000", "").trim();
		if (cleanMsg.isEmpty()) {
			return;
		}

		String senderName = "Desconhecido";
		String textBody = cleanMsg;

		// Faz o parser do remetente
		if (cleanMsg.contains(MessageContainer.FROM)) {
			String[] parts = cleanMsg.split(MessageContainer.FROM);
			if (parts.length > 1) {
				textBody = parts[0];
				senderName = parts[1];
			} else if (parts.length > 0) {
				textBody = parts[0];
			}
		}

		// Exibe mensagem recebida alinhada à esquerda
		String timeStr = new SimpleDateFormat("HH:mm:ss").format(new Date());
		String html = String.format(
				"<div class='received-box'>" +
						"  <span class='received-meta'>%s</span> " +
						"  <span class='time'>[%s]</span><br/>" +
						"  <span class='msg-body'>%s</span>" +
						"</div>" +
						"<div style='margin-bottom: 8px;'></div>",
				escapeHTML(senderName), timeStr, escapeHTML(textBody));

		appendHTML(html);
	}

	/**
	 * Limpa a tela do chat e reinicia com uma mensagem de sistema.
	 */
	private void clearChat() {
		try {
			HTMLDocument doc = (HTMLDocument) chatTextPane.getDocument();
			doc.remove(0, doc.getLength());
		} catch (Exception e) {
			chatTextPane.setText("");
		}
	}

	// Métodos Utilitários de Estilo

	private void styleLabel(JLabel label) {
		label.setForeground(COLOR_TEXT_MUTED);
		label.setFont(new Font("Segoe UI", Font.BOLD, 12));
	}

	private void styleField(JTextField field) {
		field.setBackground(COLOR_FIELD);
		field.setForeground(COLOR_TEXT);
		field.setCaretColor(COLOR_TEXT);
		field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		field.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
				BorderFactory.createEmptyBorder(6, 10, 6, 10)));
	}

	private void styleButton(JButton button, Color bg, Color fg, Color hoverBg) {
		button.setBackground(bg);
		button.setForeground(fg);
		button.setFocusPainted(false);
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(bg.darker(), 1, true),
				BorderFactory.createEmptyBorder(7, 14, 7, 14)));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));

		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				if (button.isEnabled()) {
					button.setBackground(hoverBg);
				}
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(bg);
			}
		});
	}

	/**
	 * Insere dinamicamente uma string HTML no final do JTextPane.
	 */
	private void appendHTML(String html) {
		HTMLDocument doc = (HTMLDocument) chatTextPane.getDocument();
		HTMLEditorKit kit = (HTMLEditorKit) chatTextPane.getEditorKit();
		try {
			kit.insertHTML(doc, doc.getLength(), html, 0, 0, null);
			chatTextPane.setCaretPosition(doc.getLength()); // Auto scroll para o final
		} catch (Exception e) {
			// fallback simples se der erro no kit HTML
			System.err.println("Erro ao renderizar mensagem: " + e.getMessage());
		}
	}

	private String escapeHTML(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#x27;");
	}

	/**
	 * Ponto de entrada para execução da interface gráfica.
	 */
	public static void main(String[] args) {
		// Ajusta para o visual nativo do sistema operacional (Windows/Linux/Mac)
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// Mantém o padrão do Swing se falhar
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ChatSwingUI().setVisible(true);
			}
		});
	}
}
