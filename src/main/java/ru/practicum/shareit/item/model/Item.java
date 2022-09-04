package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * // TODO .
 */
@Entity
@Table(name = "items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "name")
    @NotNull
    @NotBlank
    private String name;
    @Column(name = "description")
    @NotNull
    @NotBlank
    private String description;
    @Column(name = "available")
    @NotNull
    private Boolean available;
    @Column(name = "request_id")
    private String request;
}
