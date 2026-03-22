-- public.balance_batch_results definition

-- Drop table

-- DROP TABLE public.balance_batch_results;

CREATE TABLE public.balance_batch_results (
	batch_id int4 NOT NULL,
	sec_name varchar(20) NOT NULL,
	"action" varchar(20) NULL,
	share_quantity float8 NULL,
	calculated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT balance_batch_results_pkey PRIMARY KEY (batch_id, sec_name)
);


-- public.securities definition

-- Drop table

-- DROP TABLE public.securities;

CREATE TABLE public.securities (
	security_id serial4 NOT NULL,
	ticker varchar(10) NULL,
	"name" varchar(100) NULL,
	CONSTRAINT securities_pkey PRIMARY KEY (security_id),
	CONSTRAINT securities_ticker_key UNIQUE (ticker)
);