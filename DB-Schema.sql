CREATE TABLE public.account
(
  uuid text NOT NULL,
  name text NOT NULL,
  CONSTRAINT "ACCOUNT_pkey" PRIMARY KEY (uuid),
  CONSTRAINT "ACCOUNT_NAME_key" UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.account
  OWNER TO lodgeuser;

CREATE TABLE public.category
(
  uuid text NOT NULL,
  name text NOT NULL,
  CONSTRAINT "CATEGORY_pkey" PRIMARY KEY (uuid),
  CONSTRAINT "CATEGORY_NAME_key" UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.category
  OWNER TO lodgeuser;

CREATE TABLE public.financial_group
(
  uuid text NOT NULL,
  name text NOT NULL,
  CONSTRAINT "FIN_GROUP_pkey" PRIMARY KEY (uuid),
  CONSTRAINT "FIN_GROUP_NAME_key" UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.financial_group
  OWNER TO lodgeuser;

CREATE TABLE public.transaction
(
  id text NOT NULL,
  date date NOT NULL,
  amount numeric NOT NULL,
  type text NOT NULL,
  account_id text NOT NULL,
  location text NOT NULL,
  receipt text NOT NULL,
  comments text,
  CONSTRAINT "TRANSACTION_pkey" PRIMARY KEY (id),
  CONSTRAINT fk_trans_acct FOREIGN KEY (account_id)
      REFERENCES public.account (uuid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.transaction
  OWNER TO lodgeuser;

CREATE TABLE public.detail
(
  trans_id text NOT NULL,
  detail_order integer NOT NULL,
  month_intended text NOT NULL,
  amount numeric NOT NULL,
  financial_group_id text NOT NULL,
  category_id text NOT NULL,
  comments text,
  CONSTRAINT detail_pkey PRIMARY KEY (trans_id, detail_order),
  CONSTRAINT fk_detail_category_id FOREIGN KEY (category_id)
      REFERENCES public.category (uuid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_detail_fin_group_id FOREIGN KEY (financial_group_id)
      REFERENCES public.financial_group (uuid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_detail_trans_id FOREIGN KEY (trans_id)
      REFERENCES public.transaction (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.detail
  OWNER TO lodgeuser;
