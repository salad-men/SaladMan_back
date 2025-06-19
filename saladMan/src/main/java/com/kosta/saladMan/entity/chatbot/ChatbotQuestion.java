	package com.kosta.saladMan.entity.chatbot;
	
	import lombok.*;
	import javax.persistence.*;
	
	import com.kosta.saladMan.dto.chatbot.ChatbotQuestionDto;
	
	@Entity
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Table(name = "chatbot_question")
	public class ChatbotQuestion {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;
	
	    private String question;
	
	    private String valueKey;
	
	    @Column(name = "display_order")
	    private int displayOrder;
	
	    private int visible;
	
	    private int mainOptionId;
	
	    public ChatbotQuestionDto toDto() {
	        return ChatbotQuestionDto.builder()
	                .id(this.id)
	                .mainOptionId(this.mainOptionId)
	                .question(this.question)
	                .valueKey(this.valueKey)
	                .displayOrder(this.displayOrder)
	                .visible(this.visible)
	                .build();
	    }
	}
