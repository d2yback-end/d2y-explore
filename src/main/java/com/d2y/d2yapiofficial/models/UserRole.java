package com.d2y.d2yapiofficial.models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_role", schema = "public")
public class UserRole {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_role_id", unique = true, nullable = false)
  private Long userRoleId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id", referencedColumnName = "category_code_id")
  private CategoryCode roleId;

  @Column(name = "created_on")
  private Timestamp createdOn;

  @Column(name = "updated_on")
  private Timestamp updatedOn;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "updated_by")
  private Long updatedBy;

  @Column(name = "is_active")
  private boolean active;
}
